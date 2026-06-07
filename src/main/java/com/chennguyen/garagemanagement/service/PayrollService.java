package com.chennguyen.garagemanagement.service;

import com.chennguyen.garagemanagement.DTO.request.PayrollCalculationRequest;
import com.chennguyen.garagemanagement.DTO.response.PayslipResponse;
import com.chennguyen.garagemanagement.emuns.*;
import com.chennguyen.garagemanagement.entity.*;
import com.chennguyen.garagemanagement.exception.AppException;
import com.chennguyen.garagemanagement.exception.ErrorCode;
import com.chennguyen.garagemanagement.repository.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Setter
@Getter
@Builder
@RequiredArgsConstructor
@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class PayrollService {

    private final StaffRepository staffRepository;
    private final StaffAllowanceRepository allowanceRepository;
    private final BonusRepository bonusRepository;
    private final PayslipRepository payslipRepository;
    private final SalaryAdvanceRepository advanceRepository;
    private final ModelMapper modelMapper;

    // CONFIG CỨNG (Luật VN)
    private static final BigDecimal UNION_FEE_FIXED = BigDecimal.valueOf(50000);
    private static final BigDecimal BHXH_RATE = BigDecimal.valueOf(0.08);
    private static final BigDecimal BHYT_RATE = BigDecimal.valueOf(0.015);
    private static final BigDecimal BHTN_RATE = BigDecimal.valueOf(0.01);
    private static final BigDecimal PERSONAL_DEDUCTION = BigDecimal.valueOf(11000000);

    // =================================================================
    // 1. TÍNH LƯƠNG (ADMIN/HR)
    // =================================================================
    @Transactional
    public PayslipResponse calculatePayslip(PayrollCalculationRequest request) {
        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        LocalDate period = request.getSalaryPeriod().withDayOfMonth(1);

        // Check trùng & Xóa Draft
        payslipRepository.findByStaffAndSalaryPeriod(staff, period).ifPresent(p -> {
            if (p.getStatus() == PayslipStatus.FINALIZED || p.getStatus() == PayslipStatus.PAID) {
                throw new AppException(ErrorCode.PAYSLIP_EXISTED);
            }
            payslipRepository.delete(p);
        });

        // 1. Init Payslip
        Payslip payslip = Payslip.builder()
                .staff(staff)
                .employeeCode(staff.getEmployeeCode())
                .salaryPeriod(period)
                .status(PayslipStatus.DRAFT)
                .baseSalary(staff.getSalary())
                .salaryCoefficient(1.0)
                .bankName(staff.getBankName())
                .bankAccountNumber(staff.getBankAccountNumber())
                .build();

        // 2. Tính Hourly Rate & Tổng giờ làm
        Double standardDays = request.getStandardWorkDays();
        BigDecimal hourlyRate;
        BigDecimal actualWorkIncome;
        Double totalHoursWorked = 0.0;

        // Quy đổi ngày công ra giờ làm (để tính phụ cấp HOURLY_RATE)
        // Giả sử 1 ngày công = 8 giờ
        totalHoursWorked = request.getActualWorkDays() * 8.0;

        if (staff.getEmployeeType() == EmployeeType.PART_TIME) {
            hourlyRate = staff.getSalary();
            // Part-time: Lương = HourlyRate * Tổng giờ
            actualWorkIncome = hourlyRate.multiply(BigDecimal.valueOf(totalHoursWorked));
        } else {
            // Full-time: HourlyRate = Lương tháng / (Công chuẩn * 8)
            hourlyRate = staff.getSalary().divide(BigDecimal.valueOf(standardDays * 8), 2, RoundingMode.HALF_UP);

            // Lương ngày công: (Lương tháng / Công chuẩn) * Ngày thực tế
            BigDecimal dailyRate = staff.getSalary().divide(BigDecimal.valueOf(standardDays), 2, RoundingMode.HALF_UP);
            actualWorkIncome = dailyRate.multiply(BigDecimal.valueOf(request.getActualWorkDays()));
        }

        payslip.setHourlyRate(hourlyRate);
        payslip.setTotalHoursWorked(totalHoursWorked);

        // 3. Tính OT & Đêm
        payslip.setStandardWorkDays(standardDays);
        payslip.setActualWorkDays(request.getActualWorkDays());
        payslip.setOtNormalHours(request.getOtNormalHours());
        payslip.setOtWeekendHours(request.getOtWeekendHours());
        payslip.setOtHolidayHours(request.getOtHolidayHours());
        payslip.setNightWorkHours(request.getNightWorkHours());
        payslip.setHolidayNightWorkHours(request.getHolidayNightWorkHours());

        BigDecimal otPayAmount = calculateOtAndNightPay(hourlyRate, request);

        // 4. Phụ cấp (Sửa logic theo Enum mới)
        BigDecimal totalAllowanceAmount = calculateAllowances(staff, totalHoursWorked);
        payslip.setTotalAllowances(totalAllowanceAmount);

        // 5. Thưởng
        String periodStr = period.getMonthValue() + "-" + period.getYear();
        List<Bonus> bonuses = bonusRepository.findByStaffAndSalaryPeriod(staff.getId(), periodStr);
        BigDecimal totalBonusAmount = bonuses.stream().map(Bonus::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        payslip.setTotalBonuses(totalBonusAmount);

        // 6. TỔNG THU NHẬP (GROSS)
        BigDecimal grossSalary = actualWorkIncome
                .add(otPayAmount)
                .add(totalAllowanceAmount)
                .add(totalBonusAmount);
        payslip.setGrossSalary(grossSalary);

        // 7. Khấu trừ & Thuế
        calculateDeductionsAndTax(staff, payslip, period, request.getFineAmount());

        // 8. Thực Lĩnh
        BigDecimal netSalary = grossSalary.subtract(payslip.getTotalDeductions());
        payslip.setNetSalary(netSalary);

        Payslip saved = payslipRepository.save(payslip);
        return mapToResponse(saved);
    }

    // =================================================================
    // 2. CÁC HÀM GET DỮ LIỆU
    // =================================================================

    // Nhân viên tự xem lương (Dùng hàm Helper mới)
    @Transactional(readOnly = true)
    public List<PayslipResponse> getMyPayslips() {
        Staff staff = getCurrentAuthenticatedStaff(); // 👇 Gọi hàm helper chuẩn bro đưa
        return payslipRepository.findByStaffOrderBySalaryPeriodDesc(staff).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Admin xem lương của nhân viên bất kỳ (Truyền code)
    @Transactional(readOnly = true)
    public List<PayslipResponse> getPayslipsByEmployeeCode(String employeeCode) {
        Staff staff = staffRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return payslipRepository.findByStaffOrderBySalaryPeriodDesc(staff).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =================================================================
    // 3. PRIVATE HELPER
    // =================================================================

    // 👇 HÀM HELPER LẤY STAFF TỪ TOKEN (Y chang Customer)
    private Staff getCurrentAuthenticatedStaff() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Với Staff, username trong Token chính là Mã Nhân Viên (employeeCode)
        String employeeCode = authentication.getName();

        return staffRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    // Tính phụ cấp (Sửa theo Enum AllowanceBasis mới)
    private BigDecimal calculateAllowances(Staff staff, Double totalHoursWorked) {
        List<StaffAllowance> allowances = allowanceRepository.findByStaff_Id(staff.getId());
        BigDecimal total = BigDecimal.ZERO;

        for (StaffAllowance a : allowances) {
            if (!a.isActive()) continue;
            BigDecimal amount = a.getAmount();

            // Logic mới:
            // 1. MONTHLY_FIXED: Cộng thẳng số tiền (VD: Xăng 500k)
            // 2. HOURLY_RATE: Nhân với tổng giờ làm (VD: Ăn 4k * 200h = 800k)
            if (a.getAllowanceBasis() == AllowanceBasis.HOURLY_RATE) {
                amount = amount.multiply(BigDecimal.valueOf(totalHoursWorked));
            }
            // Nếu là MONTHLY_FIXED thì giữ nguyên amount

            total = total.add(amount);
        }
        return total;
    }

    // Tính tiền OT
    private BigDecimal calculateOtAndNightPay(BigDecimal hourlyRate, PayrollCalculationRequest req) {
        BigDecimal normal = hourlyRate.multiply(BigDecimal.valueOf(getVal(req.getOtNormalHours()) * 1.5));
        BigDecimal weekend = hourlyRate.multiply(BigDecimal.valueOf(getVal(req.getOtWeekendHours()) * 2.0));
        BigDecimal holiday = hourlyRate.multiply(BigDecimal.valueOf(getVal(req.getOtHolidayHours()) * 3.0));
        BigDecimal nightShift = hourlyRate.multiply(BigDecimal.valueOf(getVal(req.getNightWorkHours()) * 0.3));
        BigDecimal holidayNight = hourlyRate.multiply(BigDecimal.valueOf(getVal(req.getHolidayNightWorkHours()) * 3.9));

        return normal.add(weekend).add(holiday).add(nightShift).add(holidayNight);
    }

    // Tính khấu trừ & Thuế
    private void calculateDeductionsAndTax(Staff staff, Payslip payslip, LocalDate period, BigDecimal fineAmountInput) {
        BigDecimal social = BigDecimal.ZERO, health = BigDecimal.ZERO, unemp = BigDecimal.ZERO, union = BigDecimal.ZERO;

        if (staff.getEmployeeType() == EmployeeType.FULL_TIME) {
            BigDecimal base = staff.getSalary();
            social = base.multiply(BHXH_RATE);
            health = base.multiply(BHYT_RATE);
            unemp = base.multiply(BHTN_RATE);
            union = UNION_FEE_FIXED;
        }
        payslip.setSocialInsurance(social);
        payslip.setHealthInsurance(health);
        payslip.setUnemploymentInsurance(unemp);
        payslip.setUnionFee(union);

        BigDecimal fine = (fineAmountInput != null) ? fineAmountInput : BigDecimal.ZERO;
        payslip.setFineAmount(fine);

        LocalDate start = period;
        LocalDate end = period.plusMonths(1).minusDays(1);
        List<SalaryAdvance> advances = advanceRepository.findApprovedAdvancesInMonth(staff.getId(), start, end);
        BigDecimal totalAdvance = advances.stream().map(SalaryAdvance::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        payslip.setTotalAdvances(totalAdvance);

        // Thuế TNCN
        BigDecimal totalRelief = social.add(health).add(unemp).add(PERSONAL_DEDUCTION);
        BigDecimal taxableIncome = payslip.getGrossSalary().subtract(totalRelief);

        BigDecimal taxAmount = calculateProgressiveTax(taxableIncome);
        payslip.setTaxAmount(taxAmount);

        BigDecimal totalDeduction = social.add(health).add(unemp).add(union)
                .add(totalAdvance).add(fine).add(taxAmount);
        payslip.setTotalDeductions(totalDeduction);
    }

    private BigDecimal calculateProgressiveTax(BigDecimal income) {
        if (income.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        if (income.compareTo(BigDecimal.valueOf(5000000)) <= 0)
            return income.multiply(BigDecimal.valueOf(0.05));
        if (income.compareTo(BigDecimal.valueOf(10000000)) <= 0)
            return income.multiply(BigDecimal.valueOf(0.10)).subtract(BigDecimal.valueOf(250000));
        return income.multiply(BigDecimal.valueOf(0.15)).subtract(BigDecimal.valueOf(750000));
    }

    private Double getVal(Double val) {
        return val != null ? val : 0.0;
    }

    private PayslipResponse mapToResponse(Payslip p) {
        PayslipResponse res = modelMapper.map(p, PayslipResponse.class);
        res.setStaffName(p.getStaff().getFullName());
        res.setJobTitle(p.getStaff().getJobTitle());
        res.setEmployeeCode(p.getEmployeeCode());
        return res;
    }
}
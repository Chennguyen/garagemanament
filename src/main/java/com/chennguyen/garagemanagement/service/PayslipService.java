package com.chennguyen.garagemanagement.service;

import com.chennguyen.garagemanagement.DTO.response.PayslipResponse;
import com.chennguyen.garagemanagement.emuns.AllowanceBasis;
import com.chennguyen.garagemanagement.emuns.EmployeeType;
import com.chennguyen.garagemanagement.emuns.PayslipStatus;
import com.chennguyen.garagemanagement.entity.Bonus;
import com.chennguyen.garagemanagement.entity.Payslip;
import com.chennguyen.garagemanagement.entity.Staff;
import com.chennguyen.garagemanagement.entity.StaffAllowance;
import com.chennguyen.garagemanagement.exception.AppException;
import com.chennguyen.garagemanagement.exception.ErrorCode;
import com.chennguyen.garagemanagement.repository.BonusRepository;
import com.chennguyen.garagemanagement.repository.PayslipRepository;
import com.chennguyen.garagemanagement.repository.StaffAllowanceRepository;
import com.chennguyen.garagemanagement.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class PayslipService {

    StaffRepository staffRepository;
    StaffAllowanceRepository allowanceRepository;
    BonusRepository bonusRepository;
    PayslipRepository payslipRepository;

    private static final BigDecimal UNION_FEE = BigDecimal.valueOf(50000);
    private static final BigDecimal BHXH_RATE = BigDecimal.valueOf(0.08);
    private static final BigDecimal BHYT_RATE = BigDecimal.valueOf(0.015);
    private static final BigDecimal BHTN_RATE = BigDecimal.valueOf(0.01);

    @Transactional
    public PayslipResponse generatePayslip(String employeeCode, Double totalHoursInput) {
        Staff staff = staffRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        LocalDate period = LocalDate.now().withDayOfMonth(1);

        // Check if payslip already exists for this period
        if (payslipRepository.findByStaffAndSalaryPeriod(staff, period).isPresent()) {
            throw new AppException(ErrorCode.PAYSLIP_EXISTED);
        }

        // --- 1. XỬ LÝ GIỜ LÀM ---
        Double actualHours = (totalHoursInput != null) ? totalHoursInput : 0.0;

        // --- 2. TÍNH THU NHẬP (INCOME) ---
        BigDecimal baseIncome;

        if (staff.getEmployeeType() == EmployeeType.PART_TIME) {
            // Part-time: Lương giờ * Số giờ
            baseIncome = staff.getSalary() != null 
                    ? staff.getSalary().multiply(BigDecimal.valueOf(actualHours))
                    : BigDecimal.ZERO;
        } else {
            // Full-time: Lương cứng
            baseIncome = staff.getSalary() != null ? staff.getSalary() : BigDecimal.ZERO;
        }

        // Cộng Phụ cấp
        List<StaffAllowance> allowances = allowanceRepository.findByStaff_Id(staff.getId());
        BigDecimal totalAllowances = BigDecimal.ZERO;

        for (StaffAllowance a : allowances) {
            if (!a.isActive()) {
                continue;
            }
            if (a.getAllowanceBasis() == AllowanceBasis.MONTHLY_FIXED) {
                totalAllowances = totalAllowances.add(a.getAmount());
            } else {
                // Phụ cấp theo giờ
                if (actualHours > 0) {
                    totalAllowances = totalAllowances.add(a.getAmount().multiply(BigDecimal.valueOf(actualHours)));
                }
            }
        }

        // Cộng Thưởng (lấy thưởng của tháng hiện tại)
        String periodString = String.format("%d-%02d", period.getYear(), period.getMonthValue());
        List<Bonus> bonuses = bonusRepository.findByStaffAndSalaryPeriod(staff.getId(), periodString);
        BigDecimal totalBonuses = bonuses.stream()
                .map(Bonus::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grossSalary = baseIncome.add(totalAllowances).add(totalBonuses);

        // --- 3. TÍNH KHẤU TRỪ (DEDUCTIONS) ---
        BigDecimal socialInsurance = BigDecimal.ZERO;
        BigDecimal healthInsurance = BigDecimal.ZERO;
        BigDecimal unemploymentInsurance = BigDecimal.ZERO;
        BigDecimal unionFeeDeduction = BigDecimal.ZERO;

        if (staff.getEmployeeType() == EmployeeType.FULL_TIME) {
            // Chỉ tính BHXH, BHYT, BHTN cho Full-time
            socialInsurance = baseIncome.multiply(BHXH_RATE).setScale(2, RoundingMode.HALF_UP);
            healthInsurance = baseIncome.multiply(BHYT_RATE).setScale(2, RoundingMode.HALF_UP);
            unemploymentInsurance = baseIncome.multiply(BHTN_RATE).setScale(2, RoundingMode.HALF_UP);
            unionFeeDeduction = UNION_FEE;
        }

        // Tính tổng tiền tạm ứng (nếu có entity SalaryAdvance thì tính, không thì = 0)
        BigDecimal totalAdvances = BigDecimal.ZERO;

        BigDecimal totalDeductions = socialInsurance
                .add(healthInsurance)
                .add(unemploymentInsurance)
                .add(unionFeeDeduction)
                .add(totalAdvances);

        // --- 4. KẾT QUẢ ---
        BigDecimal netSalary = grossSalary.subtract(totalDeductions);
        if (netSalary.compareTo(BigDecimal.ZERO) < 0) {
            netSalary = BigDecimal.ZERO;
        }

        // 5. Lưu Payslip
        Payslip payslip = Payslip.builder()
                .staff(staff)
                .employeeCode(staff.getEmployeeCode())
                .salaryPeriod(period)
                .baseSalary(baseIncome)
                .totalAllowances(totalAllowances)
                .totalBonuses(totalBonuses)
                .totalHoursWorked(actualHours)
                .grossSalary(grossSalary)
                .socialInsurance(socialInsurance)
                .healthInsurance(healthInsurance)
                .unemploymentInsurance(unemploymentInsurance)
                .unionFee(unionFeeDeduction)
                .totalAdvances(totalAdvances)
                .totalDeductions(totalDeductions)
                .netSalary(netSalary.setScale(2, RoundingMode.HALF_UP))
                .status(PayslipStatus.DRAFT)
                .build();

        Payslip savedPayslip = payslipRepository.save(payslip);

        // Convert to response
        return convertToPayslipResponse(savedPayslip, staff);
    }

    @Transactional(readOnly = true)
    public List<PayslipResponse> getPayslipByEmployeeCode(String employeeCode) {
        Staff staff = staffRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Payslip> payslips = payslipRepository.findByStaffOrderBySalaryPeriodDesc(staff);
        
        return payslips.stream()
                .map(p -> convertToPayslipResponse(p, p.getStaff()))
                .collect(Collectors.toList());
    }

    private PayslipResponse convertToPayslipResponse(Payslip payslip, Staff staff) {
        PayslipResponse response = PayslipResponse.builder()
                .id(payslip.getId())
                .staffId(staff.getId())
                .employeeCode(payslip.getEmployeeCode())
                .staffName(staff.getFullName())
                .salaryPeriod(payslip.getSalaryPeriod())
                .baseSalary(payslip.getBaseSalary())
                .totalAllowances(payslip.getTotalAllowances())
                .totalBonuses(payslip.getTotalBonuses())
                .totalHoursWorked(payslip.getTotalHoursWorked())
                .grossSalary(payslip.getGrossSalary())
                .socialInsurance(payslip.getSocialInsurance())
                .healthInsurance(payslip.getHealthInsurance())
                .unemploymentInsurance(payslip.getUnemploymentInsurance())
                .unionFee(payslip.getUnionFee())
                .totalDeductions(payslip.getTotalDeductions())
                .totalAdvances(payslip.getTotalAdvances())
                .netSalary(payslip.getNetSalary())
                .status(payslip.getStatus())
                .createdAt(payslip.getCreatedAt())
                .build();
        return response;
    }
}

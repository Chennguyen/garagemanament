package com.chennguyen.garagemanagement.entity;

import com.chennguyen.garagemanagement.emuns.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "inventory_transactions")
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    @Enumerated(EnumType.STRING)
    TransactionType type;

    Double quantity; // Số lượng thay đổi (dương hoặc âm)
    
    // Tồn kho tính tới thời điểm giao dịch này (giúp xem thẻ kho dễ dàng)
    Double stockAfterTransaction; 

    String referenceId; // ID tham chiếu (VD: ID của PO, ID của Lệnh sửa chữa)
    
    String note; // Ghi chú (VD: "Xuất cho xe 51H-123.45")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    Staff performedBy; // Ai thực hiện xuất/nhập

    @CreationTimestamp
    LocalDateTime transactionDate;
}

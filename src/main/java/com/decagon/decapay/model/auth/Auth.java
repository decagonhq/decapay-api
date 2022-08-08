package com.decagon.decapay.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_AUTH;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = TABLE_AUTH, uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "user_id","device_id"
        })
})
@Entity
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "device_id")
    private String deviceId;
    private LocalDateTime timeStamp;
    private String token;
}

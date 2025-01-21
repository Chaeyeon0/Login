package Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user") // 테이블 이름을 "user"로 매핑
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 생성
    private Long id;

    @Column(nullable = false, unique = true) // 이메일 필수, 유니크 제약 조건
    private String email;

    @Column(nullable = false) // 비밀번호 필수
    private String password;

    @Column(nullable = false, unique = true) // 사용자 이름 필수, 유니크 제약 조건
    private String username;
}

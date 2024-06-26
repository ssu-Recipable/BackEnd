package capstone.recipable.domain.refrigerator.entity;

import capstone.recipable.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Refrigerator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public static Refrigerator of(Long id, User user) {
        return Refrigerator.builder()
                .id(id)
                .user(user)
                .build();
    }
}

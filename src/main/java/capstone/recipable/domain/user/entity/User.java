package capstone.recipable.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String loginId;

    private String password;

    private String userImg;

    private static User of(Long id, String nickname, String loginId, String password, String userImg) {
        return User.builder()
                .id(id)
                .nickname(nickname)
                .loginId(loginId)
                .password(password)
                .userImg(userImg)
                .build();
    }
}
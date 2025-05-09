package service;

import domain.BoardPrincipal;
import dto.KakaoOAuth2Response;
import entity.UserEntity;
import domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 카카오 사용자 정보 파싱
        KakaoOAuth2Response kakao = KakaoOAuth2Response.from(attributes);
        String providerId = String.valueOf(kakao.id());
        String nickname = kakao.nickname();

        // 이메일을 못 받으므로 username 으로 사용자 구분
        String username = "kakao_" + providerId;

        // DB에서 사용자 조회 또는 저장
        UserEntity user = userRepository.findByUsername(username)
                .orElseGet(() -> {
                    UserEntity newUser = UserEntity.builder()
                            .username(username)
                            .email(null) // 이메일은 받을 수 없으므로 null
                            .password("") // 소셜 로그인은 비밀번호 사용 안함
                            .build();
                    return userRepository.save(newUser);
                });

        return new BoardPrincipal(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                user.getEmail(), // null 가능
                user.getUsername(),
                null,
                attributes
        );
    }
}

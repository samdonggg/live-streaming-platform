package com.livestream.user.service;

import com.livestream.global.error.BusinessException;
import com.livestream.global.error.ErrorCode;
import com.livestream.user.domain.User;
import com.livestream.user.repository.UserRepository;
import java.time.Clock;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 가입과 조회를 맡는다.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    /**
     * 회원을 등록한다. 비밀번호는 인코딩해서 저장한다.
     *
     * @param rawPassword 사용자가 입력한 평문 비밀번호
     * @throws BusinessException 아이디나 닉네임이 중복이면 {@code DUPLICATE_LOGIN_ID}, {@code DUPLICATE_NICKNAME}
     */
    @Transactional
    public User register(String loginId, String rawPassword, String nickname) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
        User user = User.create(loginId, passwordEncoder.encode(rawPassword), nickname, clock.instant());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }
}

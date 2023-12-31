package com.spring.signalMate.domain.users.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.signalMate.domain.users.model.dto.SignInDto;
import com.spring.signalMate.domain.users.model.dto.SignInResponseDto;
import com.spring.signalMate.domain.users.model.dto.SignUpDto;
import com.spring.signalMate.domain.users.model.dto.UserUpddateDto;
import com.spring.signalMate.domain.users.model.entity.UserEntity;
import com.spring.signalMate.domain.users.repository.UsersRepository;
import com.spring.signalMate.global.dto.ResponseDto;
import com.spring.signalMate.global.security.jwt.TokenProvider;

@Service
public class AuthService {

	@Autowired
	UsersRepository userRepository;

	@Autowired
	TokenProvider tokenProvider;

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	private static final Logger log = LoggerFactory.getLogger(AuthService.class);

	@Transactional
	public ResponseDto<?> signUp(SignUpDto dto) {
		String userEmail = dto.getEmail();
		String userPassword = dto.getPassword();
		String userPasswordCheck = dto.getPasswordCheck();

		// 이메일 중복확인
		try {
			if (userRepository.existsByEmail(userEmail)) {
				return ResponseDto.setFailed("이미 존재하는 이메일 입니다.");
			}
		} catch (Exception e) {
			return ResponseDto.setFailed("데이터베이스 에러.");
		}

		// 비밀번호 확인 일치여부 확인
		if (!userPassword.equals(userPasswordCheck)) {
			return ResponseDto.setFailed("비밀번호가 일치하지 않습니다.");
		}

		// UserEntity생성
		UserEntity userEntity = new UserEntity(dto);

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(userPassword);
		userEntity.setPassword(encodedPassword);

		// 회원정보 저장
		try {
			userRepository.save(userEntity);
		} catch (Exception e) {
			log.error("Error during signIn process", e);
			return ResponseDto.setFailed("데이터베이스 에러.");
		}

		// 회원가입 완료
		return ResponseDto.setSuccess("회원가입이 정상적으로 완료되었습니다.", null);
	}

	public ResponseDto<SignInResponseDto> signIn(SignInDto dto) {
		String userEmail = dto.getEmail();
		String userPassword = dto.getPassword();
		UserEntity userEntity = null;
		try {
			userEntity = userRepository.findByEmail(userEmail).orElse(null);
			System.out.println("Searching for email: " + userEmail);
			if (userEntity == null)
				return ResponseDto.setFailed("이메일이 일치하지 않습니다.");
			if (!passwordEncoder.matches(userPassword, userEntity.getPassword()))
				return ResponseDto.setFailed("비밀번호가 일치하지 않습니다.");
		} catch (Exception e) {
			log.error("Error during signIn process", e);
			return ResponseDto.setFailed("데이터베이스 오류");
		}
		userEntity.setPassword("");

		String token = tokenProvider.createAccessToken(userEntity.getUserId(), userEmail);
		int expireTime = 3600000;

		SignInResponseDto signInResponseDto = new SignInResponseDto(token, expireTime, userEntity);
		return ResponseDto.setSuccess("로그인에 성공하였습니다.", signInResponseDto);
	}

	public void logout(String request) {

	}

	public void updateUser(Long userId, UserUpddateDto userUpddateDto) {
		UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		try {

			if (userUpddateDto.getNickname() != null && !userUpddateDto.getNickname().isEmpty()) {
				user.setNickname(userUpddateDto.getNickname());
			}

			if (userUpddateDto.getPhoneNum() != null && !userUpddateDto.getPhoneNum().isEmpty()) {
				user.setPhoneNum(userUpddateDto.getPhoneNum());
			}

			if (userUpddateDto.getProfile() != null && !userUpddateDto.getProfile().isEmpty()) {
				user.setProfile(userUpddateDto.getProfile());
			}

		} catch (Exception e) {
			log.error("Error during signIn process", e);
			System.out.println(ResponseDto.setFailed("데이터베이스 오류"));
		}

		userRepository.save(user);

	}

	public ResponseDto<String> deleteUser(Long userId) {
		// 현재 인증된 사용자 정보를 가져옴
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Object principal = authentication.getPrincipal();
		log.info("Principal type: " + principal.getClass().getName());
		log.info("Principal value: " + principal.toString());

		// 사용자 정보가 String 타입인지 확인
		if (principal instanceof String) {
			if (Long.parseLong(principal.toString()) == userId) {
				// 사용자 삭제 로직
				try {
					userRepository.deleteById(userId);
					return ResponseDto.setSuccess("User deleted successfully", null);
				} catch (Exception e) {
					log.error("Error during deleteUser process", e);
					return ResponseDto.setFailed("데이터베이스 오류");
				}
			} else {
				return ResponseDto.setFailed("권한이 없습니다.");
			}
		} else {
			return ResponseDto.setFailed("인증되지 않은 사용자입니다.");
		}
	}

}

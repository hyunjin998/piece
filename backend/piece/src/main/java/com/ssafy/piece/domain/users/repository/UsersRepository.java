package com.ssafy.piece.domain.users.repository;

import com.ssafy.piece.domain.users.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    //기본적인 CRUD 기능은 따로 메소드 작성하지 않아도 괜찮다.
    boolean existsByEmail(String email); // 이메일 중복 검사를 위한 메소드
    boolean existsByNickname(String nickname); // 닉네임 중복 검사를 위한 메소드

    Users findByUsername(String username); //사용자(로그인아이디)가 존재하는 확인하는 메소드


}
package com.dayone.service;

import com.dayone.dto.Auth;
import com.dayone.exception.impl.AlreadyExistUserException;
import com.dayone.exception.impl.NoUserIdException;
import com.dayone.exception.impl.PasswordMismatchException;
import com.dayone.persist.entity.MemberEntity;
import com.dayone.persist.MemberRepository;
import lombok.AllArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MemberService
        implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
    }

    public MemberEntity register(Auth.SignUp member){
        boolean exists = memberRepository.existsByUserName(member.getUserName());
        if (exists) {
            throw new AlreadyExistUserException();
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(member.toEntity());
    }

    public MemberEntity authenticate(Auth.SignIn member){
        MemberEntity user = memberRepository.findByUserName(member.getUserName())
                .orElseThrow(NoUserIdException::new);

        //입력으로 들어온 pw 도 인코딩함.
        //인자 1. row
        //인자 2. 인코딩
        if(!passwordEncoder.matches(member.getPassword(), user.getPassword())){
            throw new PasswordMismatchException();
        }

        return user;
    }
}

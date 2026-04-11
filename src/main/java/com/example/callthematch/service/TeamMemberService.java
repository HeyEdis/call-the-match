package com.example.callthematch.service;

import com.example.callthematch.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private TeamMemberRepository teamMemberRepository;


}

package koreatech.cse.service;

import koreatech.cse.domain.Authority;
import koreatech.cse.domain.User;
import koreatech.cse.domain.oauth2.daum.DaumProfileDetail;
import koreatech.cse.domain.oauth2.facebook.FacebookProfile;
import koreatech.cse.repository.AuthorityMapper;
import koreatech.cse.repository.UserMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Inject
    private UserMapper userMapper;
    @Inject
    private AuthorityMapper authorityMapper;
    @Inject
    private PasswordEncoder passwordEncoder;


    public Boolean signup(User user) {
        if(user.getEmail() == null || user.getPassword() ==  null)
            return false;

        user.setPassword(passwordEncoder.encode(user.getPassword()));	// 사용자의 실제 비밀번호를 sha-256로 암호화함
        userMapper.insert(user);

        Authority authority = new Authority();
        authority.setUserId(user.getId());
        authority.setRole("ROLE_USER");
        authorityMapper.insert(authority);

        if(user.getEmail().contains("admin")) {
            Authority adminAuthority = new Authority();
            adminAuthority.setUserId(user.getId());
            adminAuthority.setRole("ROLE_ADMIN");
            authorityMapper.insert(adminAuthority);
        }

        System.out.println("user created :" + new Date());
        return true;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username/password.");
        }
        List<Authority> authorities = authorityMapper.findByUserId(user.getId());
        user.setAuthorities(authorities);
        System.out.println("user = " + user);
        return user;
    }

    public String daumLogin(HttpServletRequest request, DaumProfileDetail daumProfileDetail) throws Exception{
        User user = userMapper.findByEmail(daumProfileDetail.getUserid());
        if (user == null) {
            user = new User();
            user.setEmail(daumProfileDetail.getUserid());
            user.setName(daumProfileDetail.getNickname());
            user.setAge(-1);
            user.setPassword("0000");
            signup(user);
        }

        List<Authority> authorities = authorityMapper.findByUserId(user.getId());
        user.setAuthorities(authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "0000", user.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

        return "redirect:/";
    }

    public String facebookLogin(HttpServletRequest request, FacebookProfile facebookProfile) throws Exception{
        User user = userMapper.findByEmail(facebookProfile.getId());
        if (user == null) {
            user = new User();
            user.setEmail(facebookProfile.getId());
            user.setName(facebookProfile.getName());
            user.setAge(-2);
            user.setPassword("0000");
            signup(user);
        }

        List<Authority> authorities = authorityMapper.findByUserId(user.getId());
        user.setAuthorities(authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "0000", user.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

        return "redirect:/";
    }

    public int countUsers() {
        return userMapper.count();
    }
    public int countAuthoritiesRoleUser() {
        return authorityMapper.countAuthoritiesRoleUser();
    }

}

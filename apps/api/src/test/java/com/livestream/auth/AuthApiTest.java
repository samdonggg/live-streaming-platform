package com.livestream.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.livestream.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "app.test-accounts.enabled=true")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
class AuthApiTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void 회원가입_후_로그인하면_내_정보를_조회할_수_있다() throws Exception {
        // given
        signup("alice01", "password123", "앨리스");

        // when
        MockHttpSession session = login("alice01", "password123");

        // then
        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("alice01"))
                .andExpect(jsonPath("$.nickname").value("앨리스"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void 비로그인으로_내_정보를_조회하면_401을_받는다() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }

    @Test
    void 형식에_맞지_않는_입력으로_가입하면_400을_받는다() throws Exception {
        mockMvc.perform(post("/api/auth/signup").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson("AB", "short", "")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"));
    }

    @Test
    void 이미_있는_아이디로_가입하면_409를_받는다() throws Exception {
        signup("bob01", "password123", "밥밥");

        mockMvc.perform(post("/api/auth/signup").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson("bob01", "password123", "다른닉네임")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("DUPLICATE_LOGIN_ID"));
    }

    @Test
    void 이미_있는_닉네임으로_가입하면_409를_받는다() throws Exception {
        signup("carol01", "password123", "캐롤");

        mockMvc.perform(post("/api/auth/signup").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson("carol02", "password123", "캐롤")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("DUPLICATE_NICKNAME"));
    }

    @Test
    void 비밀번호가_틀리면_401을_받는다() throws Exception {
        signup("dave01", "password123", "데이브");

        mockMvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("dave01", "wrong-password")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void 없는_아이디로_로그인하면_비밀번호_오류와_같은_401을_받는다() throws Exception {
        mockMvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("nobody", "password123")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void CSRF_토큰_없이_로그인하면_403을_받는다() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("anyone", "password123")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("INVALID_CSRF_TOKEN"));
    }

    @Test
    void 로그인하면_세션_ID가_바뀐다() throws Exception {
        signup("erin01", "password123", "에린");
        MockHttpSession before = new MockHttpSession();
        String beforeId = before.getId();

        MvcResult result = mockMvc.perform(post("/api/auth/login").with(csrf()).session(before)
                        .with(request -> {
                            // 브라우저가 세션 쿠키를 보낸 것처럼 요청에 세션 ID를 담는다
                            request.setRequestedSessionId(beforeId);
                            request.setRequestedSessionIdValid(true);
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("erin01", "password123")))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getRequest().getSession(false).getId()).isNotEqualTo(beforeId);
    }

    @Test
    void 로그아웃하면_다시_내_정보를_조회할_수_없다() throws Exception {
        signup("frank01", "password123", "프랭크");
        MockHttpSession session = login("frank01", "password123");

        mockMvc.perform(post("/api/auth/logout").with(csrf()).session(session))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 테스트_계정으로_비밀번호_없이_로그인할_수_있다() throws Exception {
        mockMvc.perform(get("/api/auth/test-accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loginId").value("streamer1"));

        MvcResult result = mockMvc.perform(post("/api/auth/test-login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginId\":\"streamer1\"}"))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);

        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("streamer1"));
    }

    @Test
    void 테스트_계정이_아닌_아이디로는_바로_로그인할_수_없다() throws Exception {
        signup("grace01", "password123", "그레이스");

        mockMvc.perform(post("/api/auth/test-login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginId\":\"grace01\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("TEST_ACCOUNT_NOT_FOUND"));
    }

    private void signup(String loginId, String password, String nickname) throws Exception {
        mockMvc.perform(post("/api/auth/signup").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson(loginId, password, nickname)))
                .andExpect(status().isCreated());
    }

    private MockHttpSession login(String loginId, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(loginId, password)))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    private static String signupJson(String loginId, String password, String nickname) {
        return """
                {"loginId":"%s","password":"%s","nickname":"%s"}
                """.formatted(loginId, password, nickname);
    }

    private static String loginJson(String loginId, String password) {
        return """
                {"loginId":"%s","password":"%s"}
                """.formatted(loginId, password);
    }
}

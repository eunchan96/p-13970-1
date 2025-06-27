package com.back.domain.post.post.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApiV1PostControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private PostService postService;

    @Test
    @DisplayName("글 쓰기")
    public void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "제목",
                                            "content": "내용"
                                        }
                                        """)
                ).andDo(print());

        Post post = postService.findLatest().get();
        long totalCount = postService.count();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글이 작성되었습니다.".formatted(post.getId())))
                .andExpect(jsonPath("$.data.totalCount").value(totalCount))
                .andExpect(jsonPath("$.data.post.id").value(post.getId()))
                .andExpect(jsonPath("$.data.post.createdDate").value(Matchers.startsWith(post.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.post.modifiedDate").value(Matchers.startsWith(post.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.post.subject").value("제목"))
                .andExpect(jsonPath("$.data.post.body").value("내용"));
    }

    @Test
    @DisplayName("글 수정")
    public void t2() throws Exception {
        int id = 1;

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/posts/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "제목 new",
                                            "content": "내용 new"
                                        }
                                        """)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글이 수정되었습니다.".formatted(id)));
    }
}
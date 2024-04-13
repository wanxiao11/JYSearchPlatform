package com.springbootinit.controller;

import com.springbootinit.common.BaseResponse;
import com.springbootinit.common.ResultUtils;
import com.springbootinit.manager.SearchFacade;
import com.springbootinit.model.dto.search.SearchRequest;
import com.springbootinit.service.PictureService;
import com.springbootinit.service.PostService;
import com.springbootinit.service.UserService;
import com.springbootinit.model.vo.SearchVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 图片接口
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private PictureService pictureService;

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    @Resource
    private SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        return ResultUtils.success(searchFacade.searchAll(searchRequest, request));
    }


}

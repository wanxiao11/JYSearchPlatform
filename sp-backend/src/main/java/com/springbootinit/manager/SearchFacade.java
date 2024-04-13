package com.springbootinit.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springbootinit.common.ErrorCode;
import com.springbootinit.datasource.*;
import com.springbootinit.exception.BusinessException;
import com.springbootinit.exception.ThrowUtils;
import com.springbootinit.model.dto.post.PostQueryRequest;
import com.springbootinit.model.dto.user.UserQueryRequest;
import com.springbootinit.model.vo.PostVO;
import com.springbootinit.model.vo.UserVO;
import com.springbootinit.datasource.*;
import com.springbootinit.model.dto.search.SearchRequest;
import com.springbootinit.model.entity.Picture;
import com.springbootinit.model.enums.SearchTypeEnum;
import com.springbootinit.model.vo.SearchVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * 搜索门面
 */
@Component
@Slf4j
public class SearchFacade {

    @Resource
    private PostDataSource postDataSource;
    @Resource
    private UserDataSource userDataSource;
    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;

    public SearchVO searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String type = searchRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);
        String searchText = searchRequest.getSearchText();
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();
        if (searchTypeEnum == null) {

            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);
                Page<UserVO> userVOPage = userDataSource.dosearch(searchText, current, pageSize);
                return userVOPage;
            });

            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureDataSource.dosearch(searchText, 1, 10);
                return picturePage;
            });

            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                Page<PostVO> postVOPage = postDataSource.dosearch(searchText, current, pageSize);
                return postVOPage;
            });
            CompletableFuture.allOf(userTask, pictureTask, postTask).join();
            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();
                SearchVO searchVO = new SearchVO();
                searchVO.setPictureList(picturePage.getRecords());
                searchVO.setUserList(userVOPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
                return searchVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page<?> page = dataSource.dosearch(searchText, current, pageSize);
            searchVO.setDataList(page.getRecords());
            return searchVO;
        }
    }

}

package org.example.picture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.picture.api.aliyunai.model.CreateOutPaintingTaskResponse;
import org.example.picture.model.dto.picture.*;
import org.example.picture.model.entity.Picture;
import org.example.picture.model.entity.User;
import org.example.picture.model.vo.PictureVO;


import java.util.List;

/**
 * @author ssz
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2026-06-05 17:17:05
 */
public interface PictureService extends IService<Picture> {

    /**
     * 校验图片
     *
     * @param picture
     */
    void validPicture(Picture picture);

    /**
     * 获取图片包装类（单条）
     *
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取图片包装类（分页）
     *
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 获取查询对象
     *
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

}

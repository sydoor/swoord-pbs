package com.lizikj.version.dto;

import com.github.pagehelper.PageInfo;
import com.lizikj.common.enums.ConvertErrorEnum;
import com.lizikj.common.exception.ConvertException;
import com.lizikj.common.util.BeanUtil;
import com.lizikj.version.LZVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
public class BaseModel implements Serializable,LZVersion {
    private static final Logger logger = LoggerFactory.getLogger(BaseDTO.class);

    private static final long serialVersionUID = 1L;

    /**
     * 将model list转换成dto list
     *
     * @param modelList
     * @param clazz
     * @return
     * @date 2016年10月31日
     */
    public static <T extends BaseModel, E extends BaseDTO> List<E> convertListDTO(List<T> modelList, Class<E> clazz) {
        List<E> list = new ArrayList<>();
        for (T e : modelList) {
            list.add(e.<E>convertToDTO(clazz));
        }
        return list;
    }

    /**
     * 将model PageInfo转换成dto PageInfo
     *
     * @param pageInfo
     * @param clazz
     * @return
     * @date 2016年10月27日
     */
    public static <T extends BaseModel, E extends BaseDTO> PageInfo<E> convertToPageInfoModel(PageInfo<T> pageInfo, Class<E> clazz) {
        List<E> list = BaseModel.convertListDTO(pageInfo.getList(), clazz);
        PageInfo<E> modelPageInfo = new PageInfo<>(list);
        BeanUtil.copyPropertiesExclude(pageInfo, modelPageInfo, "list");
        return modelPageInfo;
    }

    public <T extends BaseDTO> T convertToDTO(Class<T> clazz) {
        T t;
        try {
            t = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("instance error:", e);
            throw new ConvertException(ConvertErrorEnum.MODEL_CONVERT_TO_DTO_ERROR, e.getMessage());
        }
        return convertToDTO(t);
    }

    /**
     * 将model转换成dto
     *
     * @param t
     * @return
     * @date 2016年10月31日
     */
    public <T extends BaseDTO> T convertToDTO(T t) {
        BeanUtil.copyProperties(this, t);
        return t;
    }


    private String version;

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }
}

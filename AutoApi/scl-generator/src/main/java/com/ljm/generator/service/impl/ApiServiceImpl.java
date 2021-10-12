package com.ljm.generator.service.impl;

import com.ljm.generator.pojo.Api;
import com.ljm.generator.mapper.ApiMapper;
import com.ljm.generator.service.IApiService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ljm
 * @since 2021-08-01
 */
@Service
public class ApiServiceImpl extends ServiceImpl<ApiMapper, Api> implements IApiService {

}

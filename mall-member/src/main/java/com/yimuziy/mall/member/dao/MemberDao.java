package com.yimuziy.mall.member.dao;

import com.yimuziy.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 17:29:00
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}

package com.nxmu.mapper;

import com.nxmu.model.PasswordReset;
import com.nxmu.model.passwordResetExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetMapper {
    long countByExample(passwordResetExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PasswordReset record);

    int insertSelective(PasswordReset record);

    List<PasswordReset> selectByExample(passwordResetExample example);

    PasswordReset selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") PasswordReset record, @Param("example") passwordResetExample example);

    int updateByExample(@Param("record") PasswordReset record, @Param("example") passwordResetExample example);

    int updateByPrimaryKeySelective(PasswordReset record);

    int updateByPrimaryKey(PasswordReset record);

    int initPasswordReset(PasswordReset record);

    int delPasswordReset(PasswordReset record);

    PasswordReset selectByUserName(String userName);
}
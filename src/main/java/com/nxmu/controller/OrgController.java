package com.nxmu.controller;

import com.nxmu.exception.ApiResponse;
import com.nxmu.service.OrgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.nxmu.common.Route.*;

@RestController
@Api(tags = "组织管理接口", description = "组织管理接口")
public class OrgController {

    @Autowired
    private OrgService orgService;

    @RequestMapping(value = ORG_LIST, method = RequestMethod.GET)
    @ApiOperation(value = "组织列表", notes = "组织列表")
    public ApiResponse orgList() {
        return ApiResponse.success(orgService.findOrgList());
    }
}

package com.nxmu.service;

import com.nxmu.mapper.OrganizationMapper;
import com.nxmu.model.Organization;
import com.nxmu.model.OrganizationExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgService {

    @Autowired
    private OrganizationMapper organizationMapper;

    public List<Organization> findOrgList(){

        OrganizationExample organizationExample = new OrganizationExample();
        return organizationMapper.selectByExample(organizationExample);
    }
}

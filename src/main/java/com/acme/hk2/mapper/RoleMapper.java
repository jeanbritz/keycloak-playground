package com.acme.hk2.mapper;

import com.nimbusds.jwt.JWT;
import org.jvnet.hk2.annotations.Contract;

import java.util.List;

public interface RoleMapper {

    List<String> map(JWT jwt);
}

package com.acme.hk2.service;

import com.nimbusds.jwt.JWT;
import org.jvnet.hk2.annotations.Contract;

import java.util.List;

@Contract
public interface RoleMapper {

    List<String> map(JWT jwt);
}

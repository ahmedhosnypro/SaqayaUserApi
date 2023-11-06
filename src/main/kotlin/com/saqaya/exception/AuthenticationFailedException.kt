package com.saqaya.exception

import org.springframework.security.core.AuthenticationException


class AuthenticationFailedException(msg: String) : AuthenticationException(msg)
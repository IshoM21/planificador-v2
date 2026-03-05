package com.codigomoo.security;

import com.codigomoo.model.User;

public record RefreshResult(User user, String newRefreshRaw) {}

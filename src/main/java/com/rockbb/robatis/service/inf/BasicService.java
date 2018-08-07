package com.rockbb.robatis.service.inf;

public interface BasicService
{
	// Use this instead of Integer.MAX_VALUE because it will be added "1" as ROWNUM in Oracle DB, which will cause trouble
	int INT_MAX = Integer.MAX_VALUE - 1;
	String[] ORDER = {"DESC", "ASC"};
}

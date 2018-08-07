package com.rockbb.robatis.dao.mapper;

import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface K2DTOMapper<T>
{
	/**
	 * Insert one entity to database
	 *
	 * @param entity entity
	 */
	void add(T entity);

	/**
	 * Delete one entity by primary keys
	 *
	 * @param key1 key2
	 * @param key2 key2
	 */
	void delete(@Param("key1")Serializable key1, @Param("key2")Serializable key2);

	/**
	 * Update entity
	 *
	 * @param entity entity
	 */
	void update(T entity);

	/**
	 * Get one entity by primary keys
	 *
	 * @param key1 key2
	 * @param key2 key2
	 * @return DTO entity
	 */
	T select(@Param("key1")Serializable key1, @Param("key2")Serializable key2);

	/**
	 * Get a list of entities by specified page and arguments
	 *
	 * @param start start
	 * @param limit limit
	 * @param orderBy orderBy
	 * @param order order
	 * @param args args
	 * @return a list of dto entities
	 */
	List<T> selectByPage(
			@Param("start") int start,
			@Param("limit") int limit,
			@Param("orderBy") String orderBy,
			@Param("order") String order,
			@Param("param") Map<String, Object> args);

	/**
	 * Count the entities by specified arguments
	 *
	 * @param args arguments
	 * @return amount
	 */
	long count(@Param("param") Map<String, Object> args);
}

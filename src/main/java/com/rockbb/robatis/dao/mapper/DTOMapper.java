package com.rockbb.robatis.dao.mapper;

import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface DTOMapper<T>
{
	/**
	 * Insert one entity to database
	 *
	 * @param entity entity
	 */
	void insert(T entity);

	/**
	 * Delete one entity by primary key
	 *
	 * @param id id
	 */
	void delete(Serializable id);

	/**
	 * Update entity
	 *
	 * @param entity entity
	 */
	void update(T entity);

	/**
	 * Get one entity by primary key
	 *
	 * @param id id
	 * @return DTO entity
	 */
	T select(Serializable id);

	/**
	 * Get a list of entities by specified page and arguments
	 *
	 * @param start start
	 * @param limit limit
	 * @param orderBy orderBy
	 * @param order order
	 * @param args args
	 * @return a list of entities
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

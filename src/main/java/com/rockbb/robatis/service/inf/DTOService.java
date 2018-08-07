package com.rockbb.robatis.service.inf;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Basic service interface for DTO entities
 *
 * @param <T> DTO entity
 */
public interface DTOService<T> extends BasicService
{
	/**
	 * Insert one entity to database
	 * 
	 * @param entity entity
	 */
	void add(T entity);

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
	T get(Serializable id);

	/**
	 * Get a list of entities by specified page and arguments
	 *
	 * @param start default:0
	 * @param limit default:20
	 * @param orderBy default:0
	 * @param order default:0(desc)
	 * @param args arguments
	 * @return list of entities
	 */
	List<T> getByPage(int start, int limit, int orderBy, int order, Map<String, Object> args);

	/**
	 * Count the entities by specified arguments
	 *
	 * @param args arguments
	 * @return amount
	 */
	long count(Map<String, Object> args);
}

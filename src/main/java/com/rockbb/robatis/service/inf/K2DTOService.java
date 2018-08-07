package com.rockbb.robatis.service.inf;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @param <T> DTO entity
 */
public interface K2DTOService<T> extends BasicService
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
	 * @param id1 id1
	 * @param id2 id2
	 */
	void delete(Serializable id1, Serializable id2);

	/**
	 * Update instance in database
	 * 
	 * @param entity entity
	 */
	void update(T entity);

	/**
	 * Get one single instance by primary key 
	 * 
	 * @param id1 id1
	 * @param id2 id2
	 * @return DTO entity
	 */
	T get(Serializable id1, Serializable id2);

	/**
	 * Get a list of entities by specified page and arguments
	 *
	 * @param start default:0
	 * @param limit default:20
	 * @param orderby default:0
	 * @param order default:0(desc)
	 * @param args arguments
	 * @return list of entities
	 */
	List<T> getByPageAndCondition(int start, int limit, int orderby, int order, Map<String, Object> args);

	/**
	 * Count the entities by specified arguments
	 *
	 * @param args arguments
	 * @return amount
	 */
	long countByCondition(Map<String, Object> args);
}

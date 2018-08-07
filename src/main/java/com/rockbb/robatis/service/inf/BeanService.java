package com.rockbb.robatis.service.inf;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @param <T> Bean of U
 * @param <U> Entity
 */
public interface BeanService<T, U> extends BasicService
{
	/**
	 * Insert one bean to database
	 * 
	 * @param bean bean
	 */
	void add(T bean);

	/**
	 * Delete one bean by primary key
	 * 
	 * @param id id
	 */
	void delete(Serializable id);

	/**
	 * Update one bean in database
	 * 
	 * @param bean bean
	 */
	void update(T bean);

	/**
	 * Get one bean by primary key
	 * 
	 * @param id id
	 * @return bean
	 */
	T get(Serializable id);

	/**
	 * Get a list of beans by specified page and arguments
	 *
	 * @param start default:0
	 * @param limit default:20
	 * @param orderby default:0
	 * @param order default:0(desc)
	 * @param args arguments
	 * @return list of beans
	 */
	List<T> getByPageAndCondition(int start, int limit, int orderby, int order, Map<String, Object> args);

	/**
	 * Count the beans by specified arguments
	 *
	 * @param args arguments
	 * @return amount
	 */
	long countByCondition(Map<String, Object> args);

	/**
	 * convert entity to bean
	 * 
	 * @param entity entity
	 * @return bean
	 */
	T adapt(U entity);

	/**
	 * convert bean to entity
	 * 
	 * @param bean bean
	 * @return entity
	 */
	U toEntity(T bean);

	/**
	 * convert entities to beans
	 * 
	 * @param entities list of entities
	 * @return list of beans
	 */
	List<T> adaptList(List<U> entities);
}

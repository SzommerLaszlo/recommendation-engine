package com.utcluj.recommender.dataset.services;

/**
 * Service to verify if an object exists or not.
 * @param <T> an object type.
 */
public interface ExistsService<T> {

	boolean exists(T id);
}

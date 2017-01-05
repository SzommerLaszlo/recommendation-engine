package com.utcluj.recommender.dataset.services.impl;

import com.utcluj.recommender.dataset.domain.Post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

@Service
public class PostOwnerExistsService extends AbstractCachingExistsService<Post> {

	/** Database template to handle the database operations. */
	private JdbcOperations jdbcTemplate;

	@Override
	public boolean exists(Post post) {
		String id = String.valueOf(post.getOwnerUserId());

		if(!isCached(id)) {
			Long count = jdbcTemplate.queryForObject("select count(*) from users where id = ?", Long.class, post.getOwnerUserId());
			if(count != null && count > 0) {
				cache(id);
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	@Autowired
	public void setJdbcTemplate(JdbcOperations jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}

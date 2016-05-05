package com.badoo.barf.data.repo;

/**
 * A marker interface for repository queries. Any class implementing this must also provide implementations of equals() and hashCode()
 * otherwise you might risk having queries executed multiple times (concurrently) since we cannot deduplicate them.
 * <p>
 * <R> the expected return type for this query
 */
public interface Query<Result> {
}

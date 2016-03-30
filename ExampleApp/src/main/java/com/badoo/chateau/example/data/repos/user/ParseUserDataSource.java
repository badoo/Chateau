package com.badoo.chateau.example.data.repos.user;

import android.support.annotation.NonNull;

import com.badoo.chateau.example.data.util.ParseHelper;
import com.badoo.chateau.example.data.util.ParseUtils;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.repos.users.UserDataSource;
import com.badoo.chateau.core.repos.users.UserQuery;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import rx.Observable;
import rx.schedulers.Schedulers;

public class ParseUserDataSource implements UserDataSource {

    private ParseHelper mParseHelper;

    public ParseUserDataSource(@NonNull ParseHelper parseHelper) {
        mParseHelper = parseHelper;
    }

    @NonNull
    @Override
    public Observable<User> getAllUsers(UserQuery.GetAllUsersQuery query) {
        final ParseQuery<ParseUser> parseQuery = new ParseQuery<>("_User");
        parseQuery.whereNotEqualTo("objectId", mParseHelper.getCurrentUser().getObjectId());
        parseQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);

        return mParseHelper.find(parseQuery)
            .flatMap(Observable::from)
            .map(ParseUtils::fromParseUser)
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Observable<User> getSingleUser(UserQuery.GetUserQuery query) {
        ParseQuery<ParseUser> parseQuery = new ParseQuery<>("_User");
        parseQuery.whereEqualTo("objectId", query.getUserId());
        parseQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);

        return mParseHelper.find(parseQuery)
            .flatMap(Observable::from)
            .map(ParseUtils::fromParseUser)
            .subscribeOn(Schedulers.io());
    }

}

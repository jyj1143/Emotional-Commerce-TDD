package com.loopers.event;

public record EventListener(){

    public static final class TOPICS {
        public static final String LIKED = "outside.liked-events.v1";
        public static final String LIKE_CANCELED = "outside.like-canceled-events.v1";
    }

    public static final class GROUP_ID {
        public static final String LIKE = "like-listener-group";
    }

}

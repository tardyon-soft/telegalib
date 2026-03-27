package ru.tardyon.botframework.telegram.api.model.story;

public record StoryAreaTypeUniqueGift(String name) implements StoryAreaType {

    @Override
    public String type() {
        return "unique_gift";
    }

    public static StoryAreaTypeUniqueGift of(String name) {
        return new StoryAreaTypeUniqueGift(name);
    }
}

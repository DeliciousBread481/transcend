package huige233.transcend.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NBTMatcher {
    public static void matchNBT(String nbtString) {
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");

        Matcher matcher = pattern.matcher(nbtString);
        while (matcher.find()) {
            String match = matcher.group(1);
            Pattern tagPattern = Pattern.compile("(\\w+):(\\{.*?\\}|\\[.*?\\]|\\w+|'.*?')");

            Matcher tagMatcher = tagPattern.matcher(match);
            while (tagMatcher.find()) {
                String tagName = tagMatcher.group(1);
                String tagValue = tagMatcher.group(2);
                System.out.println("Tag: " + tagName + "，Key: " + tagValue);

                if (tagValue.startsWith("{") || tagValue.startsWith("[")) {
                    matchNBT(tagValue);
                }
            }
        }
    }

    public static boolean isCompoundMatching(String nbtString, String patternString) {
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");

        Matcher matcher = pattern.matcher(nbtString);
        while (matcher.find()) {
            String match = matcher.group(1);
            Pattern tagPattern = Pattern.compile("(\\w+):(\\{.*?\\}|\\[.*?\\]|\\w+|'.*?')");

            Matcher tagMatcher = tagPattern.matcher(match);
            boolean isMatching = true;
            boolean isPatternMatching = false;

            while (tagMatcher.find()) {
                String tagName = tagMatcher.group(1);
                String tagValue = tagMatcher.group(2);

                if (patternString.contains(tagName + ":" + tagValue)) {
                    isPatternMatching = true;
                }

                if (!patternString.contains(tagName + ":" + tagValue)) {
                    isMatching = false;
                    break;
                }
            }

            if (isPatternMatching && isMatching) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        String nbtData1 = "{ForgeData: {IsPlaying: 0b}}";
        matchNBT(nbtData1);

        String nbtData2 = "{ForgeData: {CurrentTime: 0, IsPlaying: 0b}}";
        matchNBT(nbtData2);

        String nbtData3 = "{BadData: {IsPlaying: 0b}}";
        matchNBT(nbtData3);
    }
}

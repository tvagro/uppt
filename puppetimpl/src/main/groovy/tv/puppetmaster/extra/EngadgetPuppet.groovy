package tv.puppetmaster.extra

import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

public class EngadgetPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 5

    def ParentPuppet mParent
    def String mUrl
    def String mName
    def String mDescription
    def String mImageUrl
    def String mBackgroundImageUrl

    public EngadgetPuppet() {
        this(
                null,
                "http://feeds.contenthub.aol.com/syndication/2.0/feeds/article?sid=6d83dd23075648c2924a6469c80026c7&articleText=7&max=100",
                "Engadget",
                "News, reviews and opinion outlet with obsessive coverage of cutting edge gadgets.",
                "https://raw.githubusercontent.com/hansbogert/plugin.video.engadget/master/icon.png",
                "https://i.ytimg.com/vi/OJZgtL3PXf8/maxresdefault.jpg"
        )
    }

    public EngadgetPuppet(ParentPuppet parent, String url, String name, String description, String imageUrl, String backgroundImageUrl) {
        mParent = parent
        mUrl = url
        mName = name
        mDescription = description
        mImageUrl = imageUrl
        mBackgroundImageUrl = backgroundImageUrl
    }

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new EngadgetPuppetIterator()

        JSONArray items = new JSONObject(new URL(mUrl).getText()).getJSONObject("channel").getJSONArray("item")
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i)
            JSONArray mediaContent = item.getJSONArray("media_content")
            for (int j = 0; j < mediaContent.length(); j++) {
                JSONObject mc = mediaContent.getJSONObject(j)
                if(mc.getString("media_medium") == "video" && mc.getString("url").contains("youtube.com")) {
                    children.add(new EngadgetSourcesPuppet(this, item))
                    break
                }
            }
        }
        return children
    }

    @Override
    boolean isTopLevel() {
        return true
    }

    @Override
    String getName() {
        return mName
    }

    @Override
    String getCategory() {
        return "Technology"
    }

    @Override
    String getShortDescription() {
        return mDescription
    }

    @Override
    String getImageUrl() {
        return mImageUrl
    }

    @Override
    String getBackgroundImageUrl() {
        return mBackgroundImageUrl
    }

    @Override
    boolean isUnavailableIn(String region) {
        return false
    }

    @Override
    String getPreferredRegion() {
        return null
    }

    @Override
    int getShieldLevel() {
        return 0
    }

    @Override
    ParentPuppet getParent() {
        return null
    }

    @Override
    SearchesPuppet getSearchProvider() {
        return null
    }

    @Override
    SettingsPuppet getSettingsProvider() {
        return null
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF2B2D32
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF2B2D32
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF2B2D32
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return null
    }

    @Override
    PuppetIterator getRelated() {
        return null
    }

    @Override
    public String toString() {
        return mParent == null ? getName() : mParent.toString() + " < " + getName()
    }

    @Override
    int getVersionCode() {
        return VERSION_CODE
    }

    def class EngadgetPuppetIterator extends PuppetIterator {

        def ArrayList<Puppet> mPuppets = new ArrayList<>()
        def int currentIndex = 0

        @Override
        boolean hasNext() {
            return currentIndex < mPuppets.size()
        }

        @Override
        void add(Puppet puppet) {
            mPuppets.add(puppet)
        }

        @Override
        Puppet next() {
            return mPuppets.get(currentIndex++)
        }

        @Override
        void remove() {

        }
    }

    def static class EngadgetSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mUrl
        def String mName
        def String mShortDescription
        def String mImageUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        public EngadgetSourcesPuppet(parent, JSONObject item) {
            mParent = parent

            JSONArray mediaContent = item.getJSONArray("media_content")
            for (int j = 0; j < mediaContent.length(); j++) {
                JSONObject mc = mediaContent.getJSONObject(j)
                switch (mc.getString("media_medium")) {
                    case "video":
                        mUrl = mc.getString("url")
                        break
                    case "image":
                        mImageUrl = mc.getString("url")
                        break
                }
            }

            mName = item.getString("title")
            mShortDescription = Jsoup.parse(item.getString("description")).text()
        }

        @Override
        String getPublicationDate() {
            return null
        }

        @Override
        long getDuration() {
            return -1
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new EngadgetSourceIterator()
        }

        @Override
        boolean isLive() {
            return false
        }

        @Override
        List<SourcesPuppet.SubtitleDescription> getSubtitles() {
            return mSubtitles
        }

        @Override
        String getName() {
            return mName
        }

        @Override
        String getCategory() {
            return null
        }

        @Override
        String getShortDescription() {
            return mShortDescription
        }

        @Override
        String getImageUrl() {
            return mImageUrl
        }

        @Override
        String getBackgroundImageUrl() {
            return mImageUrl
        }

        @Override
        boolean isUnavailableIn(String region) {
            return false
        }

        @Override
        String getPreferredRegion() {
            return null
        }

        @Override
        int getShieldLevel() {
            return 0
        }

        @Override
        ParentPuppet getParent() {
            return mParent
        }

        @Override
        PuppetIterator getRelated() {
            return null
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class EngadgetSourceIterator implements SourcesPuppet.SourceIterator {

            def List<SourceDescription> mSources = null
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourceDescription>()
                    if (EngadgetSourcesPuppet.this.mUrl.contains("youtube.com")) {
                        String id = EngadgetSourcesPuppet.this.mUrl.split("v=")[1]
                        id = id.split("&")[0]
                        String url = "http://www.youtube.com/get_video_info?video_id=" + id
                        String content = new URL(url).getText()
                        for (String p : content.split("&")) {
                            String key = p.substring(0, p.indexOf('='))
                            String value = p.substring(p.indexOf('=') + 1)
                            if (key == "url_encoded_fmt_stream_map") {
                                value = decode(value)
                                for (String u : value.split("url=")) {
                                    if (u.startsWith("http")) {
                                        u = getCorrectURL(decode(u))
                                        SourceDescription source = new SourceDescription()
                                        source.url = u
                                        mSources.add(source)
                                    }
                                }
                                break
                            }
                        }
                    }
                }
                return currentIndex < mSources.size()
            }

            @Override
            SourceDescription next() {
                return mSources.get(currentIndex++)
            }

            @Override
            void remove() {

            }

            def static final BAD_KEYS = ["stereo3d", "type", "fallback_host", "quality"]

            def static String decode(String s) {
                try {
                    return URLDecoder.decode(s, "UTF-8")
                } catch (ignore) {
                }
                return s
            }

            def static String getCorrectURL(String input) {
                StringBuilder builder = new StringBuilder(input.substring(0, input.indexOf('?') + 1))
                String[] params = input.substring(input.indexOf('?') + 1).split("&")
                LinkedList<String> keys = new LinkedList<String>()
                boolean first = true
                for (String param : params) {
                    String key = param
                    try {
                        key = param.substring(0, param.indexOf('='))
                    } catch (ignore) {
                    }
                    if (keys.contains(key) || BAD_KEYS.contains(key)) {
                        continue
                    }
                    keys.add(key)
                    if (key == "sig") {
                        builder.append(first ? "" : "&").append("signature=").append(param.substring(4))
                    } else {
                        if (param.contains(",quality=")) {
                            param = remove(param, ",quality=", "_end_")
                        }
                        if (param.contains(",type=")) {
                            param = remove(param, ",type=", "_end_")
                        }
                        if (param.contains(",fallback_host")) {
                            param = remove(param, ",fallback_host", ".com")
                        }
                        builder.append(first ? "" : "&").append(param)
                    }
                    if (first) first = false
                }
                return builder.toString()
            }

            def static String remove(String text, String start, String end) {
                int l = text.indexOf(start)
                return text.replace(text.substring(l, end.equals("_end_") ? text.length() : text.indexOf(end, l)), "")
            }
        }
    }
}
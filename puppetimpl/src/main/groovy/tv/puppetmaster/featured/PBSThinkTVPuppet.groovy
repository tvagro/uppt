package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

import java.util.concurrent.TimeUnit
import java.util.regex.Matcher

public class PBSThinkTVPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 3

    def ParentPuppet mParent
    def String mUrl
    def String mName
    def String mDescription
    def String mImageUrl
    def String mBackgroundImageUrl

    public PBSThinkTVPuppet() {
        this(
                null,
                "http://www.pbs.org/shows-page/0/?genre=&title=&callsign=",
                "PBS Think TV",
                "Your favorite PBS shows.",
                "https://superrepo.org/static/images/icons/original/xplugin.video.thinktv.png.pagespeed.ic.SmysxYmAM7.jpg",
                "https://superrepo.org/static/images/fanart/original/plugin.video.thinktv.jpg"
        )
    }

    public PBSThinkTVPuppet(ParentPuppet parent, String url, String name, String description, String imageUrl, String backgroundImageUrl) {
        mParent = parent
        mUrl = url
        mName = name
        mDescription = description
        mImageUrl = imageUrl
        mBackgroundImageUrl = backgroundImageUrl
    }

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new PBSThinkTVPuppetIterator()

        JSONArray json = new JSONObject(new URL(mUrl).getText()).getJSONObject("results").getJSONArray("content")

        for (int i = 0; i < json.length(); i++) {
            JSONObject item = json.getJSONObject(i)
            children.add(new PBSThinkTVCategoryPuppet(this, item))
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
        return "USA"
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
    boolean isAvailable(String region) {
        return true
    }

    @Override
    String[] preferredRegions() {
        return null
    }

    @Override
    int immigrationStricture() {
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
    int getFastlaneBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF33691E
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF33691E
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF33691E
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
        return getName()
    }

    @Override
    int getVersionCode() {
        return VERSION_CODE
    }

    def class PBSThinkTVPuppetIterator extends PuppetIterator {

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

    def class PBSThinkTVCategoryPuppet implements ParentPuppet {

        def ParentPuppet mParent
        def String mId
        def String mName
        def String mDescription
        def String mImageUrl
        def String mVideoType
        def int mPage = 0

        public PBSThinkTVCategoryPuppet(ParentPuppet parent, JSONObject item) {
            mParent = parent
            mId = item.getString("id")
            mName = item.getString("title")
            mDescription = item.getString("description")
            mImageUrl = item.isNull("image") ? parent.getImageUrl() : item.getString("image")
        }

        public PBSThinkTVCategoryPuppet(ParentPuppet parent, String id, String name, String imageUrl, String videoType, int page) {
            mParent = parent
            mId = id
            mName = name
            mImageUrl = imageUrl
            mVideoType = videoType
            mPage = page
        }

        @Override
        PuppetIterator getChildren() {
            PuppetIterator children = new PBSThinkTVPuppetIterator()
            if (mPage == 0) {
                ["episodes", "previews", "clips"].each { videoType ->
                    children.add(new PBSThinkTVCategoryPuppet(this, mId, videoType.capitalize(), mImageUrl, videoType, mPage + 1))
                }
            } else {
                String url = "http://www.pbs.org/show/" + mId + "/$mVideoType/?page=$mPage"
                if (!Jsoup.connect(url).followRedirects(false).execute().hasHeader("location")) {
                    Document document = Jsoup.connect(url).ignoreContentType(true).get()
                    int numItems = 0
                    document.select(".video-summary").each { node ->
                        String id = node.select("a").first().attr("href").replace("/video/", "").replace("/", "")
                        String name = node.select(".video-summary__title").first().text()
                        String duration = node.select(".video-summary__duration").first().text()
                        def images = node.select(".video-summary__image").attr("data-srcset").split()
                        String imageUrl = images[0]
                        String backgroundImageUrl = images[images.length - 2]
                        children.add(new PBSThinkTVSourcesPuppet(this, id, name, duration, imageUrl, backgroundImageUrl))
                        numItems++
                    }
                    if (numItems > 23) { // 24 items per page so if we hit 24, present a next link
                        children.add(new PBSThinkTVCategoryPuppet(this, mId, mVideoType.capitalize() + " page " + (mPage + 1), mImageUrl, mVideoType, mPage + 1))
                    }
                }
            }
            return children
        }

        @Override
        boolean isTopLevel() {
            return false
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
            return null
        }

        @Override
        String getImageUrl() {
            return mImageUrl
        }

        @Override
        String getBackgroundImageUrl() {
            return PBSThinkTVPuppet.this.mBackgroundImageUrl
        }

        @Override
        boolean isAvailable(String region) {
            return true
        }

        @Override
        String[] preferredRegions() {
            return null
        }

        @Override
        int immigrationStricture() {
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
            return getName()
        }
    }

    def static class PBSThinkTVSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mId
        def String mName
        def long mDuration
        def String mImageUrl
        def String mBackgroundImageUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        public PBSThinkTVSourcesPuppet(parent, String id, String name, String duration, String imageUrl, String backgroundImageUrl) {
            mParent = parent
            mId = id
            mName = name
            mDuration = convertDuration(duration)
            mImageUrl = imageUrl
            mBackgroundImageUrl = backgroundImageUrl
        }

        @Override
        String getPublicationDate() {
            return null
        }

        @Override
        long getDuration() {
            return mDuration
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new PBSThinkTVSourceIterator()
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
            return null
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
        boolean isAvailable(String region) {
            return region == 'us'
        }

        @Override
        String[] preferredRegions() {
            return ['us'] as String[]
        }

        @Override
        int immigrationStricture() {
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
            return getName()
        }

        private static long convertDuration(String str) {  // HH:MM[:SS] to milliseconds
            if (str.equals("")) {
                return -1
            }

            String[] data = str.split(":")

            int time
            if (data.length > 2) {
                int hours  = Integer.parseInt(data[0])
                int minutes = Integer.parseInt(data[1])
                int seconds = Integer.parseInt(data[2])
                time = seconds + 60 * minutes + 3600 * hours
            } else if (data.length > 1) {
                int minutes = Integer.parseInt(data[0])
                int seconds = Integer.parseInt(data[1])
                time = seconds + 60 * minutes
            } else {
                int seconds = Integer.parseInt(data[0])
                time = seconds
            }

            return TimeUnit.MILLISECONDS.convert(time, TimeUnit.SECONDS)
        }

        def class PBSThinkTVSourceIterator implements SourcesPuppet.SourceIterator {

            def SourceDescription mSource = null

            @Override
            boolean hasNext() {
                if (mSource == null) {

                    mSource = new SourceDescription()

                    String html = new URL("http://player.pbs.org/viralplayer/" + PBSThinkTVSourcesPuppet.this.mId).getText().replaceAll("\n", "")

                    Matcher matcher = html =~ /PBS.videoData =.+?recommended_encoding.+?'url'.+?'(.+?)'.+?'closed_captions_url'.+?'(.+?)'/

                    if (matcher.find()) {
                        String url = new JSONObject(new URL(matcher.group(1) + "?format=json").getText()).getString("url")
                        if (url.contains("mp4:")) {
                            url = "http://ga.video.cdn.pbs.org/" + url.split("mp4:")[1]
                        } else if (url.contains("m3u8")) {
                            url = url.replace("800k", "2500k")
                            if (url.contains("hd-1080p")) {
                                url = url.split("-hls-")[0]
                                url += "-hls-6500k.m3u8"
                            }
                        }
                        mSource.url = url
                        SourcesPuppet.SubtitleDescription subs = new SourcesPuppet.SubtitleDescription()
                        subs.url = matcher.group(2)
                        PBSThinkTVSourcesPuppet.this.mSubtitles.add(subs)
                    }
                    return true
                }
                return false
            }

            @Override
            SourceDescription next() {
                return mSource
            }

            @Override
            void remove() {

            }
        }
    }
}
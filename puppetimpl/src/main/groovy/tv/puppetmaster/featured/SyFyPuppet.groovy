package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

import java.util.regex.Matcher

public class SyFyPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 3

    def ParentPuppet mParent
    def String mUrl
    def String mName
    def String mDescription
    def boolean mIsTopLevel
    def String mImageUrl

    public SyFyPuppet() {
        this(
                null,
                "http://www.syfy.com/episodes",
                "SyFy",
                "Full episodes of past and current Syfy shows.",
                true,
                "http://www.zebrahd.com/img/homepage/syfy.png"
        )
    }

    public SyFyPuppet(ParentPuppet parent, String url, String name, String description, boolean isTopLevel, String imageUrl) {
        mParent = parent
        mUrl = url
        mName = name
        mDescription = description
        mIsTopLevel = isTopLevel
        mImageUrl = imageUrl
    }

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new SyFyPuppetIterator()

        Document document = Jsoup.connect(mUrl).ignoreContentType(true).get()

        if (mUrl.endsWith("/episodes")) {
            document.select(".show").each { node ->
                String showName = node.select("h3").first().text()

                children.add(new SyFyPuppet(this, null, showName, null, true, null))

                node.select((".grid-item")).each { episode ->
                    String name = episode.select(".episode-info span").first().text()
                    String url = episode.select("a").first().absUrl("href")
                    String image = episode.select("img").first().absUrl("src")

                    children.add(new SyFySourcesPuppet(this, url, name, null, image))
                }
            }
        } else {
            document.select(".video-reference").each { node ->
                String name = node.select("figcaption h6").first().text()
                String url = node.select("a").first().absUrl("href")
                String description = node.select(".full-episode") ? "Full episode" : null
                String image = node.select("img").first().absUrl("src")

                children.add(new SyFySourcesPuppet(this, url, name, description, image))
            }
        }
        return children
    }

    @Override
    boolean isTopLevel() {
        return mIsTopLevel
    }

    @Override
    String getName() {
        return mName
    }

    @Override
    String getCategory() {
        return mParent == null ? "USA" : mParent.getName()
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
        return "http://nerdwest.com/wp-content/uploads/2014/04/syfy-imagine-greater-logo.jpg"
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
        return 0xFF533173
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF533173
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

    def class SyFyPuppetIterator extends PuppetIterator {

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

    def static class SyFySourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mUrl
        def String mName
        def String mShortDescription
        def String mImageUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        public SyFySourcesPuppet(parent, url, name, shortDescription, imageUrl) {
            mParent = parent
            mUrl = url
            mName = name
            mShortDescription = shortDescription
            mImageUrl = imageUrl
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
            return new SyFySourceIterator()
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

        def class SyFySourceIterator implements SourcesPuppet.SourceIterator {

            def List<SourceDescription> mSources = null
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {

                    mSources = new ArrayList<SourceDescription>()

                    Document document = Jsoup.connect(SyFySourcesPuppet.this.mUrl).ignoreContentType(true).get()
                    String url = document.select(".pdk-player").first().absUrl("data-src").replace("&amp;", "&")

                    String html = new URL(url).getText().replaceAll("\n", "")

                    Matcher matcher = html =~ /tp:releaseUrl="(.+?)"/

                    if (matcher.find()) {

                        long unow = System.currentTimeMillis() / 1000L
                        long uexp = unow + 60
                        String gvu1 = "https://tvesyfy-vh.akamaihd.net/i/prod/video/VIDEO_,25,40,18,12,7,4,2,00.mp4.csmil/master.m3u8?__b__=1000&hdnea=st=" + unow + "~exp=" + uexp
                        String gvu2 = "https://tvesyfy-vh.akamaihd.net/i/prod/video/VIDEO_,1696,1296,896,696,496,240,306,.mp4.csmil/master.m3u8?__b__=1000&hdnea=st=" + unow + "~exp=" + uexp
                        String pfu1 = "http://link.theplatform.com/s/HNK2IC/media/"
                        String pfparms = "?player=Syfy.com%20Player&policy=2713542&manifest=m3u&formats=flv,m3u,mpeg4&format=SMIL&embedded=true&tracking=true"

                        url = matcher.group(1).split("\\?")[0]

                        JSONObject json = new JSONObject(new URL(url + "?format=script").getText())

                        try {
                            JSONObject captions = (JSONObject) ((JSONArray) json.get("captions")).get(0)

                            SourcesPuppet.SubtitleDescription subs = new SourcesPuppet.SubtitleDescription()
                            subs.url = captions.getString("src")
                            subs.mime = captions.getString("type")
                            subs.locale = captions.getString("lang")

                            SyFySourcesPuppet.this.mSubtitles.add(subs)

                            url = subs.url.split("/caption/")[1]
                            url = url.substring(0, url.lastIndexOf("."))

                            SourceDescription source = new SourceDescription()
                            source.url = gvu1.replace("VIDEO", url)

                            mSources.add(source)

                            source = new SourceDescription()
                            source.url = gvu2.replace("VIDEO", url)

                            mSources.add(source)
                        } catch (ignore) {
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
        }
    }
}
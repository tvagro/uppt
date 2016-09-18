package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import tv.puppetmaster.data.i.InstallablePuppet
import tv.puppetmaster.data.i.ParentPuppet
import tv.puppetmaster.data.i.Puppet
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SearchesPuppet
import tv.puppetmaster.data.i.SourcesPuppet
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

public class KhanAcademyPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    def ParentPuppet mParent
    def String mBaseUrl
    def String mName
    def String mDescription
    def String mUrl
    def String mImageUrl
    def String mBackgroundImageUrl
    def boolean mIsTopLevel

    public KhanAcademyPuppet() {
        this(
                null,
                "http://www.khanacademy.org",
                "Khan Academy",
                "The Khan Academy is an organization on a mission with the goal of changing education for the better by providing a free world-class education to anyone anywhere.",
                "/api/v1/topic/root",
                "https://raw.githubusercontent.com/jbeluch/xbmc-khan-academy/master/icon.png",
                "http://corporate.comcast.com/images/khan-academy-logo.jpg",
                true
        )
    }

    protected KhanAcademyPuppet(ParentPuppet parent, String baseUrl, String name, String description, String url, String imageUrl, String backgroundImageUrl, boolean isTopLevel) {
        mParent = parent
        mBaseUrl = baseUrl
        mName = name
        mDescription = description
        mUrl = url != null && url.startsWith("/") ? mBaseUrl + url : url
        mImageUrl = imageUrl != null && imageUrl.startsWith("/") ? mBaseUrl + imageUrl : imageUrl
        mBackgroundImageUrl = backgroundImageUrl != null && backgroundImageUrl.startsWith("/") ? mBaseUrl + backgroundImageUrl : backgroundImageUrl
        mIsTopLevel = isTopLevel
    }

    @Override
    PuppetIterator getChildren() {
        return new KhanAcademyIterator(this, mBaseUrl, mUrl)
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
        return mParent == null ? "Education" : mParent.getName()
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
        return mParent
    }

    @Override
    SearchesPuppet getSearchProvider() {
        return null
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF394551
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFBCD039
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF9EB63A
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF9EB63A
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
    int getVersionCode() {
        return VERSION_CODE
    }

    @Override
    String toString() {
        return mParent == null ? getName() : mParent.toString() + " < " + getName()
    }

    void setUrl(String url) {
        mUrl = url
    }

    def class KhanAcademyIterator extends PuppetIterator {

        def ParentPuppet mParent
        def String mBaseUrl
        def String mUrl
        def ArrayList<Puppet> mPuppets

        def int numSources = 0

        def int currentCategoryIteration = 0
        def int currentSourcesIteration = 0

        transient def JSONArray categoryItems = new JSONArray()
        transient def JSONArray sourcesItems = new JSONArray()

        public KhanAcademyIterator(ParentPuppet parent, String baseUrl, String url) {
            mParent = parent
            mBaseUrl = baseUrl
            mUrl = url
        }

        @Override
        boolean hasNext() {
            if (mUrl == null || mUrl.trim() == "") {
                return false
            } else if (mPuppets == null) {
                mPuppets = new ArrayList<>()

                String page = new URL(mUrl.startsWith("/") ? mBaseUrl + mUrl : mUrl).getText()

                JSONArray items = (JSONArray) new JSONObject(page).get("children")

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i)
                    if (item.get("kind").equals("Video")) {
                        sourcesItems.put(item)
                        mPuppets.add(new KhanAcademySourcesPuppet(
                                mParent,
                                mBaseUrl + "/api/v1/videos/" + item.get("id").toString(),
                                item.get("title").toString(),
                                item.get("description").toString(),
                                "https://raw.githubusercontent.com/jbeluch/xbmc-khan-academy/master/icon.png",
                                "http://corporate.comcast.com/images/khan-academy-logo.jpg"
                        ))
                    } else if (item.get("kind").equals("Topic")) {
                        categoryItems.put(item)
                        mPuppets.add(new KhanAcademyPuppet(
                                mParent,
                                mBaseUrl,
                                item.get("title").toString(),
                                item.get("description").toString(),
                                "/api/v1/topic/" + item.get("id").toString(),
                                "http://static1.squarespace.com/static/517fede1e4b0809658b2e697/t/55302b98e4b0ba7d377044f1/1429220249979/khan.jpg",
                                "http://corporate.comcast.com/images/khan-academy-logo.jpg",
                                false
                        ))
                    }
                }
            }
            return (currentCategoryIteration + currentSourcesIteration) < mPuppets.size()
        }

        @Override
        Puppet next() {
            if (currentSourcesIteration < numSources) {
                return mPuppets.get(currentSourcesIteration++)
            }
            return mPuppets.get(numSources + currentCategoryIteration++)
        }

        @Override
        void remove() {

        }

        @Override
        void add(Puppet puppet) {

        }
    }

    def static class KhanAcademySourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mUrl
        def String mName
        def String mDescription
        def String mImageUrl
        def String mBackgroundImageUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        public KhanAcademySourcesPuppet(ParentPuppet parent, String url, String name, String description, String imageUrl, String backgroundImageUrl) {
            mParent = parent
            mUrl = url
            mName = name
            mDescription = description
            mImageUrl = imageUrl
            mBackgroundImageUrl = backgroundImageUrl
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
            return new KhanAcademySourceIterator(mUrl)
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
            return mParent
        }

        @Override
        public PuppetIterator getRelated() {
            mParent.getChildren()
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class KhanAcademySourceIterator implements SourcesPuppet.SourceIterator {

            def String mUrl
            def List<SourceDescription> mSources
            def int currentIndex = 0

            public KhanAcademySourceIterator(String url) {
                mUrl = url
            }

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourceDescription>()

                    String page = new URL(mUrl).getText()
                    JSONObject item = new JSONObject(page)
                    JSONObject downloadUrls = (JSONObject) item.get("download_urls")
                    long duration = Long.parseLong(item.get("duration").toString()) * 1000

                    ["m3u8", "mp4", "mp4-low"].each { videoType ->
                        try {
                            SourceDescription source = new SourceDescription()
                            source.url = downloadUrls.get(videoType)
                            if (!source.url.equals("")) {
                                source.duration = duration
                                mSources.add(source)
                            }
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
package tv.puppetmaster.featured;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tv.puppetmaster.data.i.InstallablePuppet
import tv.puppetmaster.data.i.ParentPuppet;
import tv.puppetmaster.data.i.Puppet;
import tv.puppetmaster.data.i.Puppet.PuppetIterator;
import tv.puppetmaster.data.i.SearchesPuppet;
import tv.puppetmaster.data.i.SourcesPuppet;
import tv.puppetmaster.data.i.SourcesPuppet.SourceIterator;
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription;
import tv.puppetmaster.data.i.SourcesPuppet.SubtitleDescription;

public class CBCPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4;

    transient def Document mDocument;

    def ParentPuppet mParent;

    def String mBaseUrl;
    def String mSearchUrl;

    def String mUrl;
    def String mName;
    def String mCategory;
    def String mImageUrl;
    def String mBackgroundImageUrl;
    def String mRelatedUrl;

    def transient PuppetIterator mChildren;
    def transient PuppetIterator mRelated;

    def transient ScrapeUrlSearchesPuppet mSearchProvider;

    def String mSourcesItemScrapeUrlFormat;
    def String mSourcesItemScrapeUrlPattern;

    def String[] mCategoryItemSelectors;
    def String mSourcesItemScrapeUrlSelector;
    def String mSourcesItemSelector;
    def String mSourcesNameSelector;
    def String mSourcesCategorySelector;
    def String mSourcesDescriptionSelector;
    def String mSourcesDurationSelector;
    def String mSourcesPublicationDateSelector;
    def String mSourcesIsLiveSelector;
    def String mSourcesImageSelector;
    def String mSourcesBackgroundImageSelector;
    def String mSourcesRelatedScrapeUrlSelector;

    public CBCPuppet() {
        this(
                null,
                "http://www.cbc.ca",
                "/player/search?query=",
                "/player",
                "CBC.ca",
                "http://rlv.zcache.ca/cbc_radio_canada_gem_round_sticker-r51dda5f884654d579ee93b01c8f580a5_v9wth_8byvr_324.jpg",
                "/bc/community/blog/photo/Laptop%20Decal_V2.jpg",
                null,
                ["nav ul li:nth-child(4) a,nav ul li:nth-child(5) a", ".longlist-list li a"] as String[],
                "http://tpfeed.cbc.ca/f/ExhSPC/vms_5akSXx4Ng_Zn?q=*&byGuid=#1",
                "(\\d+)",
                ".livenow-item,.allfeatured-item:not(:contains(\\(Live at):contains(ET\\))),.featured-container:has(.featured-title),.medialist-item:has(.medialist-title)",
                "a:first-child",
                ".livenow-title,.allfeatured-title,.featured-title,.medialist-title",
                ".livenow-category,.allfeatured-show,.featured-show,.medialist-show",
                ".livenow-description,.allfeatured-description,.featured-description,.medialist-seasonepisode",
                ".livenow-duration,.allfeatured-duration,.featured-duration,.medialist-duration",
                ".livenow-date,.allfeatured-banner span,.featured-date,.medialist-date",
                ".icon-live",
                ".livenow-thumbnail img,.allfeatured-image img,img.featured-image,.medialist-thumbnail img",
                ".livenow-thumbnail img,.allfeatured-image img,img.featured-image,.medialist-thumbnail img",
                "a"
        );
    }

    public CBCPuppet(ParentPuppet parent, String baseUrl, String searchUrl, String url, String name, String imageUrl, String backgroundImageUrl, String relatedUrl, String[] categoryItemSelectors, String sourcesItemScrapeUrlFormat, String sourcesItemScrapeUrlPattern, String sourcesItemSelector, String sourcesItemScrapeUrlSelector, String sourcesNameSelector, String sourcesCategorySelector, String sourcesDescriptionSelector, String sourcesDurationSelector, String sourcesPublicationDateSelector, String sourcesIsLiveSelector, String sourcesImageSelector, String sourcesBackgroundImageSelector, String sourcesRelatedScrapeUrlSelector) {
        mParent = parent;

        mBaseUrl = baseUrl;
        mSearchUrl = searchUrl != null && searchUrl.startsWith("/") ? baseUrl + searchUrl : searchUrl;

        mUrl = url != null && url.startsWith("/") ? baseUrl + url : url;
        mName = name;
        mCategory = parent != null ? mParent.getName() : null;
        mImageUrl = imageUrl != null && imageUrl.startsWith("/") ? baseUrl + imageUrl : imageUrl;
        mBackgroundImageUrl = backgroundImageUrl != null && backgroundImageUrl.startsWith("/") ? baseUrl + backgroundImageUrl : backgroundImageUrl;
        mRelatedUrl = relatedUrl != null && relatedUrl.startsWith("/") ? baseUrl + relatedUrl : relatedUrl;

        mCategoryItemSelectors = categoryItemSelectors;

        mSourcesItemScrapeUrlFormat = sourcesItemScrapeUrlFormat != null && sourcesItemScrapeUrlFormat.startsWith("/") ? baseUrl + sourcesItemScrapeUrlFormat : sourcesItemScrapeUrlFormat;
        mSourcesItemScrapeUrlPattern = sourcesItemScrapeUrlPattern;

        mSourcesItemSelector = sourcesItemSelector;
        mSourcesItemScrapeUrlSelector = sourcesItemScrapeUrlSelector;
        mSourcesNameSelector = sourcesNameSelector;
        mSourcesCategorySelector = sourcesCategorySelector;
        mSourcesDescriptionSelector = sourcesDescriptionSelector;
        mSourcesDurationSelector = sourcesDurationSelector;
        mSourcesPublicationDateSelector = sourcesPublicationDateSelector;
        mSourcesIsLiveSelector = sourcesIsLiveSelector;
        mSourcesImageSelector = sourcesImageSelector;
        mSourcesBackgroundImageSelector = sourcesBackgroundImageSelector;
        mSourcesRelatedScrapeUrlSelector = sourcesRelatedScrapeUrlSelector;
    }

    @Override
    public String getName() {
        return mName;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    @Override
    public String getCategory() {
        return mParent == null ? "Public Service" : mCategory;
    }

    @Override
    public String getShortDescription() {
        return "News, sports and television from Canada's national broadcaster";
    }

    @Override
    public String getImageUrl() {
        return mImageUrl;
    }

    @Override
    public String getBackgroundImageUrl() {
        return mBackgroundImageUrl;
    }

    @Override
    boolean isUnavailableIn(String region) {
        return false;
    }

    @Override
    String getPreferredRegion() {
        return null;
    }

    @Override
    int getShieldLevel() {
        return 0;
    }

    @Override
    public ParentPuppet getParent() {
        return mParent;
    }

    @Override
    public SearchesPuppet getSearchProvider() {
        if (mSearchProvider == null && mSearchUrl != null) {
            mSearchProvider = new ScrapeUrlSearchesPuppet(this, mBaseUrl, mSearchUrl, mSearchUrl, "Search: " + mName, mImageUrl, mBackgroundImageUrl, mRelatedUrl, mCategoryItemSelectors, mSourcesItemScrapeUrlFormat, mSourcesItemScrapeUrlPattern, mSourcesItemSelector, mSourcesItemScrapeUrlSelector, mSourcesNameSelector, mSourcesCategorySelector, mSourcesDescriptionSelector, mSourcesDurationSelector, mSourcesPublicationDateSelector, mSourcesIsLiveSelector, mSourcesImageSelector, mSourcesBackgroundImageSelector, mSourcesRelatedScrapeUrlSelector);
        }
        return mSearchProvider;
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFFE21A21
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFFE21A21
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFFE21A21
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return null
    }

    @Override
    public PuppetIterator getRelated() {
        if (mRelated == null && mRelatedUrl != null) {
            mRelated = new CBCPuppet(this, mBaseUrl, mSearchUrl, mRelatedUrl, "Related to: " + mName, mImageUrl, mBackgroundImageUrl, null, mCategoryItemSelectors, mSourcesItemScrapeUrlFormat, mSourcesItemScrapeUrlPattern, mSourcesItemSelector, mSourcesItemScrapeUrlSelector, mSourcesNameSelector, mSourcesCategorySelector, mSourcesDescriptionSelector, mSourcesDurationSelector, mSourcesPublicationDateSelector, mSourcesIsLiveSelector, mSourcesImageSelector, mSourcesBackgroundImageSelector, mSourcesRelatedScrapeUrlSelector).getChildren();
        }
        return mRelated;
    }

    @Override
    public String toString() {
        return mParent == null ? getName() : mParent.toString() + " < " + getName()
    }

    @Override
    public PuppetIterator getChildren() {
        if (mChildren == null || mParent == null) {
            init();
        }
        return mChildren;
    }

    @Override
    boolean isTopLevel() {
        return mParent == null || mParent.getParent() == null
    }

    @Override
    int getVersionCode() {
        return VERSION_CODE
    }

    def void init() {
        try {
            mDocument = Jsoup.connect(mUrl).get();
            mChildren = new CBCPuppetIterator(mBaseUrl);
        } catch (Exception ex) {
            // Error scraping mLocation
        }
    }

    def void setUrl(String url) {
        mUrl = url;
        mChildren = null;
    }

    def class CBCPuppetIterator extends PuppetIterator {

        def int currentCategoryIteration = 0;
        def int currentSourcesIteration = 0;
        def List<Puppet> mPuppets = new ArrayList<Puppet>();

        def String mBaseUrl;

        transient def Elements categoryItems;
        transient def Elements sourcesItems;

        public CBCPuppetIterator(String baseUrl) {
            if (categoryItems == null || sourcesItems == null) {
                init();
            }
            mBaseUrl = baseUrl;
        }

        def void init() {
            if (mDocument == null) {
                CBCPuppet.this.init();
            }
            categoryItems = mDocument.select(mCategoryItemSelectors[0]);
            sourcesItems = mDocument.select(mSourcesItemSelector);
        }

        @Override
        public void add(Puppet puppet) {
            // pass
        }

        @Override
        public boolean hasNext() {
            if (categoryItems == null || sourcesItems == null) {
                init();
            }
            if ((currentCategoryIteration + currentSourcesIteration) < (categoryItems.size() + sourcesItems.size())) {
                return true;
            } else {
                // Reset for reuse
                currentCategoryIteration = 0;
                currentSourcesIteration = 0;
            }
            return false;
        }

        @Override
        public Puppet next() {
            boolean withinSources = currentSourcesIteration < sourcesItems.size();
            if ((currentCategoryIteration + currentSourcesIteration) >= mPuppets.size()) {
                if (withinSources) {
                    Element item = sourcesItems.get(currentSourcesIteration);

                    String scrapeUrl = item.select(mSourcesItemScrapeUrlSelector).toString();
                    if (mSourcesItemScrapeUrlFormat != null && mSourcesItemScrapeUrlPattern != null) {
                        try {
                            Pattern p = Pattern.compile(mSourcesItemScrapeUrlPattern);
                            Matcher m = p.matcher(scrapeUrl);
                            if (m.find()) {
                                scrapeUrl = mSourcesItemScrapeUrlFormat.replace("#1", m.group());
                            }
                        } catch (Exception ex) {
                            // Error scraping items from scrapeUrl
                        }
                    }

                    SourcesPuppet sourcesPuppet = new JsonContainedSMILSourcesPuppet();

                    sourcesPuppet.setParent(CBCPuppet.this);
                    sourcesPuppet.setUrl(scrapeUrl.startsWith("/") ? mBaseUrl + scrapeUrl : scrapeUrl);
                    String name = item.select(mSourcesNameSelector).text().trim();
                    sourcesPuppet.setName(name);
                    if (mSourcesCategorySelector != null) {
                        String category = item.select(mSourcesCategorySelector).text().trim();
                        if (category != "") {
                            sourcesPuppet.setCategory(category);
                            if (CBCPuppet.this.isTopLevel() && category != CBCPuppet.this.getName()) {
                                sourcesPuppet.setName(name + " - " + category);
                            }
                        }
                    }
                    sourcesPuppet.setShortDescription(item.select(mSourcesDescriptionSelector).text().trim());
                    sourcesPuppet.setDuration(convertDuration(item.select(mSourcesDurationSelector).text().trim()));
                    sourcesPuppet.setPublicationDate(item.select(mSourcesPublicationDateSelector).text().trim());
                    if (mSourcesIsLiveSelector != null) {
                        sourcesPuppet.setIsLive(item.select(mSourcesIsLiveSelector).size() > 0);
                    }
                    String imageUrl = item.select(mSourcesImageSelector).first().absUrl("src");
                    sourcesPuppet.setImageUrl(imageUrl);
                    String backgroundImageUrl = item.select(mSourcesBackgroundImageSelector).first().absUrl("src");
                    sourcesPuppet.setBackgroundImageUrl(backgroundImageUrl);
                    String relatedScrapeUrl = mSourcesRelatedScrapeUrlSelector != null ? item.select(mSourcesRelatedScrapeUrlSelector).attr("href") : null;
                    sourcesPuppet.setRelatedParent(new CBCPuppet(CBCPuppet.this, mBaseUrl, mSearchUrl, relatedScrapeUrl, mName, mImageUrl, mBackgroundImageUrl, null, mCategoryItemSelectors, mSourcesItemScrapeUrlFormat, mSourcesItemScrapeUrlPattern, mSourcesItemSelector, mSourcesItemScrapeUrlSelector, mSourcesNameSelector, mSourcesCategorySelector, mSourcesDescriptionSelector, mSourcesDurationSelector, mSourcesPublicationDateSelector, mSourcesIsLiveSelector, mSourcesImageSelector, mSourcesBackgroundImageSelector, mSourcesRelatedScrapeUrlSelector));
                    mPuppets.add(sourcesPuppet);
                } else {
                    Element item = categoryItems.get(currentCategoryIteration);
                    String url = item.attr("href");
                    String name = item.text().trim();

                    String[] categoryItemSelectors = mCategoryItemSelectors.length > 1 ? Arrays.copyOfRange(mCategoryItemSelectors, 1, mCategoryItemSelectors.length) : mCategoryItemSelectors;
                    mPuppets.add(new CBCPuppet(CBCPuppet.this, mBaseUrl, mSearchUrl, url, name, mImageUrl, mBackgroundImageUrl, null, categoryItemSelectors, mSourcesItemScrapeUrlFormat, mSourcesItemScrapeUrlPattern, mSourcesItemSelector, mSourcesItemScrapeUrlSelector, mSourcesNameSelector, mSourcesCategorySelector, mSourcesDescriptionSelector, mSourcesDurationSelector, mSourcesPublicationDateSelector, mSourcesIsLiveSelector, mSourcesImageSelector, mSourcesBackgroundImageSelector, mSourcesRelatedScrapeUrlSelector));
                }
            }
            Puppet curr = mPuppets.get(currentCategoryIteration + currentSourcesIteration);
            if (withinSources) {
                currentSourcesIteration++;
            } else {
                currentCategoryIteration++;
            }
            return curr;
        }

        @Override
        public void remove() {
            // pass
        }

        private static long convertDuration(String str) {  // HH:MM[:SS] to milliseconds
            if (str.equals("")) {
                return -1;
            }

            String[] data = str.split(":");

            int time;
            if (data.length > 2) {
                int hours  = Integer.parseInt(data[0]);
                int minutes = Integer.parseInt(data[1]);
                int seconds = Integer.parseInt(data[2]);
                time = seconds + 60 * minutes + 3600 * hours;
            } else if (data.length > 1) {
                int minutes = Integer.parseInt(data[0]);
                int seconds = Integer.parseInt(data[1]);
                time = seconds + 60 * minutes;
            } else {
                int seconds = Integer.parseInt(data[0]);
                time = seconds;
            }

            return TimeUnit.MILLISECONDS.convert(time, TimeUnit.SECONDS);
        }
    }

    public static class ScrapeUrlSearchesPuppet extends CBCPuppet implements SearchesPuppet {

        def String mSearchUrl;

        public ScrapeUrlSearchesPuppet(ParentPuppet parent, String baseUrl, String searchUrl, String url, String name, String imageUrl, String backgroundImageUrl, String relatedUrl, String[] categoryItemSelectors, String sourcesItemScrapeUrlFormat, String sourcesItemScrapeUrlPattern, String sourcesItemSelector, String sourcesItemScrapeUrlSelector, String sourcesNameSelector, String sourcesCategorySelector, String sourcesDescriptionSelector, String sourcesDurationSelector, String sourcesPublicationDateSelector, String sourcesIsLiveSelector, String sourcesImageSelector, String sourcesBackgroundImageSelector, String sourcesRelatedScrapeUrlSelector) {
            super(parent, baseUrl, searchUrl, url, name, imageUrl, backgroundImageUrl, relatedUrl, categoryItemSelectors, sourcesItemScrapeUrlFormat, sourcesItemScrapeUrlPattern, sourcesItemSelector, sourcesItemScrapeUrlSelector, sourcesNameSelector, sourcesCategorySelector, sourcesDescriptionSelector, sourcesDurationSelector, sourcesPublicationDateSelector, sourcesIsLiveSelector, sourcesImageSelector, sourcesBackgroundImageSelector, sourcesRelatedScrapeUrlSelector);
            mSearchUrl = searchUrl;
        }
        @Override
        public void setSearchQuery(String searchQuery) {
            setCategory(searchQuery);
            setUrl(mSearchUrl + searchQuery);
        }

        @Override
        boolean isTopLevel() {
            return false
        }
    }

    public static class JsonContainedSMILSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent;
        def String mUrl;
        def String mName;
        def String mCategory;
        def String mShortDescription;
        def String mImageUrl;
        def String mBackgroundImageUrl;
        def String mPublicationDate;
        def long mDuration;
        def boolean mIsLive = false;
        def ParentPuppet mRelatedParent;

        def List<SubtitleDescription> mSubtitles = new ArrayList<SubtitleDescription>();

        public void setParent(ParentPuppet parent) {
            mParent = parent;
        }

        public void setUrl(String url) {
            mUrl = url;
        }

        public void setPublicationDate(String publicationDate) {
            mPublicationDate = publicationDate;
        }

        @Override
        public String getPublicationDate() {
            return mPublicationDate;
        }

        public void setDuration(long duration) {
            mDuration = duration;
        }

        @Override
        public long getDuration() {
            return mDuration;
        }

        @Override
        public SourceIterator getSources() {
            return new JsonContainedSMILSourceIterator();
        }

        public void setIsLive(boolean isLive) {
            mIsLive = isLive;
        }

        @Override
        public boolean isLive() {
            return mIsLive;
        }

        @Override
        public List<SubtitleDescription> getSubtitles() {
            return mSubtitles;
        }

        public void setName(String name) {
            mName = name;
        }

        @Override
        public String getName() {
            return mName;
        }

        public void setCategory(String category) {
            mCategory = category;
        }

        @Override
        public String getCategory() {
            return mCategory;
        }

        public void setShortDescription(String shortDescription) {
            mShortDescription = shortDescription;
        }

        @Override
        public String getShortDescription() {
            return mShortDescription;
        }

        public void setImageUrl(String imageUrl) {
            mImageUrl = imageUrl;
        }

        @Override
        public String getImageUrl() {
            return mImageUrl;
        }

        public void setBackgroundImageUrl(String backgroundImageUrl) {
            mBackgroundImageUrl = backgroundImageUrl;
        }

        @Override
        public String getBackgroundImageUrl() {
            return mBackgroundImageUrl;
        }

        @Override
        boolean isUnavailableIn(String region) {
            if (region == 'ca') {
                return false;
            }
            try {
                JSONObject entries = fetchJSON(mUrl).getJSONArray("entries").getJSONObject(0)
                JSONArray countries = entries.getJSONArray("countries");
                for (int i = 0; i < countries.length(); i++) {
                    if (region == countries.getString(i)) {
                        return entries.getBoolean("excludeCountries");
                    }
                }
                return !entries.getBoolean("excludeCountries");
            } catch (Exception ignore) {

            }
            return true;
        }

        @Override
        String getPreferredRegion() {
            return 'ca';
        }

        @Override
        int getShieldLevel() {
            return 0;
        }

        @Override
        public ParentPuppet getParent() {
            return mParent;
        }

        public void setRelatedParent(ParentPuppet relatedParent) {
            mRelatedParent = relatedParent;
        }

        @Override
        public PuppetIterator getRelated() {
            return mRelatedParent != null ? mRelatedParent.getChildren() : null;
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class JsonContainedSMILSourceIterator implements SourceIterator {

            def List<SourceDescription> mSources = new ArrayList<SourceDescription>();
            def transient JSONObject mJson;
            def transient JSONArray mContent;
            def int mNextSourceIndex = -1;

            @Override
            public boolean hasNext() {

                if (mUrl == null || mUrl.trim() == "") {
                    return false;
                } else if (mNextSourceIndex < 0) {
                    mJson = fetchJSON(mUrl);
                    try {
                        JSONObject entries = (JSONObject) mJson.getJSONArray("entries").get(0);
                        mContent = entries.getJSONArray("content");

                        for (int i = 0; mNextSourceIndex < 0; i++) {
                            SourceDescription currentSource = new SourceDescription();
                            try {
                                JSONObject entry = mContent.getJSONObject(i);

                                currentSource.url = entry.getString("url").split("/meta.smil")[0] + "?mbr=true&manifest=m3u";
                                try {
                                    currentSource.duration = (long) Float.parseFloat(entry.get("duration").toString()) * 1000;
                                    currentSource.height = entry.get("height").toString();
                                    currentSource.width = entry.get("width").toString();

                                    String previewUrl = currentSource.url.replace("?mbr=true&manifest=m3u", "?mbr=true&format=preview");
                                    JSONObject previewJson = fetchJSON(previewUrl);
                                    for (int j = 0; j < previewJson.getJSONArray("captions").length(); j++) {
                                        SubtitleDescription subtitle = new SubtitleDescription();
                                        subtitle.url =((JSONObject) previewJson.getJSONArray("captions").get(j)).getString("src").toString();
                                        mSubtitles.add(subtitle);
                                    }
                                } catch (Exception ex) {
                                    // Error parsing extra source values
                                }

                                Document document = Jsoup.connect(currentSource.url).ignoreContentType(true).get();

                                try {
                                    currentSource.url = document.select("video").first().attr("src");

                                    mSources.add(currentSource);
                                    mNextSourceIndex = 0;

                                    Collections.sort(mSources, new Comparator<SourceDescription>() {
                                        @Override
                                        public int compare(SourceDescription lhs, SourceDescription rhs) {
                                            // Reverse sort so higher quality is tried first
                                            return Long.compare(Long.parseLong(rhs.width), Long.parseLong(lhs.width));
                                        }
                                    });
                                } catch (Exception ex) {
                                    // No video, let's try audio
                                    currentSource.url = document.select("audio").first().attr("src");
                                    currentSource.isAudioOnly = true;
                                    mSources.add(currentSource);
                                    mNextSourceIndex = 0;
                                }
                            } catch (Exception ex) {
                                // Must be out of sources
                                Collections.sort(mSources, new Comparator<SourceDescription>() {
                                    @Override
                                    public int compare(SourceDescription lhs, SourceDescription rhs) {
                                        // Reverse sort so higher quality is tried first
                                        return Long.compare(Long.parseLong(rhs.width), Long.parseLong(lhs.width));
                                    }
                                });
                                mNextSourceIndex = 0;
                            }
                        }
                    } catch (Exception ex) {
                        // Oops, something went wrong, go with what sources we have
                        mNextSourceIndex = 0;
                    }
                }

                return mNextSourceIndex < mSources.size();
            }

            @Override
            public SourceDescription next() {
                mSources.get(mNextSourceIndex++);
            }

            @Override
            public void remove() {

            }
        }

        def static JSONObject fetchJSON(String link) {
            // Parse JSON from URL link
            BufferedReader reader = null;

            try {
                URL url = new URL(link);
                URLConnection urlConnection = url.openConnection();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "iso-8859-1"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String json = sb.toString();
                return new JSONObject(json);
            } catch (Exception e) {
                // Failed to parse the JSON for media list
                return null;
            } finally {
                if (null != reader) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        // JSON feed closed
                    }
                }
            }
        }
    }
}
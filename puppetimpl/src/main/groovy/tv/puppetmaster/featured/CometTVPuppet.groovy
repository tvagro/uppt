package tv.puppetmaster.featured

import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

import java.util.regex.Matcher

public class CometTVPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 3

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new CometTVPuppetIterator(this)
        children.add(new CometTVLiveSourcesPuppet(this))
        return children
    }

    @Override
    boolean isTopLevel() {
        return true
    }

    @Override
    String getName() {
        return "Comet TV"
    }

    @Override
    String getCategory() {
        return "USA"
    }

    @Override
    String getShortDescription() {
        return "Dedicated to sci-fi entertainment offering popular favorites, cult classics, and undiscovered gems, every day"
    }

    @Override
    String getImageUrl() {
        return "https://raw.githubusercontent.com/learningit/plugin.video.comettv/master/icon.png"
    }

    @Override
    String getBackgroundImageUrl() {
        return "http://static-12.sinclairstoryline.com/resources/media/00cae24d-0f3f-4ae6-bc22-bc78f31fbb99-large16x9_COMET_LOGO_GREEN.jpg"
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
        return 0xFF53AC02
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFBAE704
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF53AC02
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return [[
                        name:           getName(),
                        description:    getShortDescription(),
                        genres:         "ENTERTAINMENT",
                        logo:           getImageUrl(),
                        url:            "http://content.uplynk.com/channel/810bf2b47d9f4bd8a0101dd2b21afc91.m3u8?tc=1&exp=1614631202&rn=1044192764&ct=c&cid=810bf2b47d9f4bd8a0101dd2b21afc91&ad=comet&ad.adUnit=%2FCOMET%2FLivestream_midroll&ad._debug=comet_vmap&ad.ad_rule=1&ad.pmad=10&ad.output=xml_vmap1&sig=61df4f67b54a8b52d2060b9e30bcf4bbe41fe1a1c86bf468377f869a29569b7c"
                ]]
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

    def class CometTVPuppetIterator extends PuppetIterator {

        def ParentPuppet mParent
        def ArrayList<Puppet> mPuppets = new ArrayList<>()
        def int currentIndex = 0

        public CometTVPuppetIterator(ParentPuppet parent) {
            mParent = parent
        }


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

    def static class CometTVLiveSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent

        public CometTVLiveSourcesPuppet(ParentPuppet parent) {
            mParent = parent
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
            return new CometTVLiveSourceIterator()
        }

        @Override
        boolean isLive() {
            return true
        }

        @Override
        List<SourcesPuppet.SubtitleDescription> getSubtitles() {
            return null
        }

        @Override
        String getName() {
            return "Comet TV Live"
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
            return "https://raw.githubusercontent.com/learningit/plugin.video.comettv/master/icon.png"
        }

        @Override
        String getBackgroundImageUrl() {
            return "http://static-12.sinclairstoryline.com/resources/media/00cae24d-0f3f-4ae6-bc22-bc78f31fbb99-large16x9_COMET_LOGO_GREEN.jpg"
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

        def class CometTVLiveSourceIterator implements SourcesPuppet.SourceIterator {

            def String mSourceUrl

            @Override
            boolean hasNext() {
                if (mSourceUrl == null) {
                    String html = new URL("http://www.comettv.com/watch-live/").getText(requestProperties: ['User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11'])
                    Matcher matcher = html =~ /file: "(.+?)"/
                    if (matcher.find()) {
                        mSourceUrl = matcher.group(1)
                    }
                    return true
                }
                return false
            }

            @Override
            SourceDescription next() {
                SourceDescription sourceDescription = new SourceDescription()
                sourceDescription.url = mSourceUrl
                return sourceDescription
            }

            @Override
            void remove() {

            }
        }
    }
}
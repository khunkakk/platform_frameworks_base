/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.media;

import android.annotation.IntDef;
import android.annotation.NonNull;
import android.annotation.TestApi;
import android.icu.util.ULocale;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


/**
 * The AudioPresentation class encapsulates the information that describes an audio presentation
 * which is available in next generation audio content.
 *
 * Used by {@link MediaExtractor} {@link MediaExtractor#getAudioPresentations(int)} and
 * {@link AudioTrack} {@link AudioTrack#setPresentation(AudioPresentation)} to query available
 * presentations and to select one, respectively.
 *
 * A list of available audio presentations in a media source can be queried using
 * {@link MediaExtractor#getAudioPresentations(int)}. This list can be presented to a user for
 * selection.
 * An AudioPresentation can be passed to an offloaded audio decoder via
 * {@link AudioTrack#setPresentation(AudioPresentation)} to request decoding of the selected
 * presentation. An audio stream may contain multiple presentations that differ by language,
 * accessibility, end point mastering and dialogue enhancement. An audio presentation may also have
 * a set of description labels in different languages to help the user to make an informed
 * selection.
 */
public final class AudioPresentation {
    private final int mPresentationId;
    private final int mProgramId;
    private final ULocale mLanguage;

    /** @hide */
    @IntDef(
        value = {
            MASTERING_NOT_INDICATED,
            MASTERED_FOR_STEREO,
            MASTERED_FOR_SURROUND,
            MASTERED_FOR_3D,
            MASTERED_FOR_HEADPHONE,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface MasteringIndicationType {}
    private final @MasteringIndicationType int mMasteringIndication;
    private final boolean mAudioDescriptionAvailable;
    private final boolean mSpokenSubtitlesAvailable;
    private final boolean mDialogueEnhancementAvailable;
    private final Map<ULocale, String> mLabels;

    /**
     * No preferred reproduction channel layout.
     *
     * @see Builder#setMasteringIndication(int)
     */
    public static final int MASTERING_NOT_INDICATED         = 0;
    /**
     * Stereo speaker layout.
     *
     * @see Builder#setMasteringIndication(int)
     */
    public static final int MASTERED_FOR_STEREO             = 1;
    /**
     * Two-dimensional (e.g. 5.1) speaker layout.
     *
     * @see Builder#setMasteringIndication(int)
     */
    public static final int MASTERED_FOR_SURROUND           = 2;
    /**
     * Three-dimensional (e.g. 5.1.2) speaker layout.
     *
     * @see Builder#setMasteringIndication(int)
     */
    public static final int MASTERED_FOR_3D                 = 3;
    /**
     * Prerendered for headphone playback.
     *
     * @see Builder#setMasteringIndication(int)
     */
    public static final int MASTERED_FOR_HEADPHONE          = 4;

    /**
     * This ID is reserved. No items can be explicitly assigned this ID.
     */
    private static final int UNKNOWN_ID = -1;

    /**
     * This allows an application developer to construct an AudioPresentation object with all the
     * parameters.
     * The IDs are all that is required for an
     * {@link AudioTrack#setPresentation(AudioPresentation)} to be successful.
     * The rest of the metadata is informative only so as to distinguish features
     * of different presentations.
     * @param presentationId Presentation ID to be decoded by a next generation audio decoder.
     * @param programId Program ID to be decoded by a next generation audio decoder.
     * @param language Locale corresponding to ISO 639-1/639-2 language code.
     * @param masteringIndication One of {@link AudioPresentation#MASTERING_NOT_INDICATED},
     *     {@link AudioPresentation#MASTERED_FOR_STEREO},
     *     {@link AudioPresentation#MASTERED_FOR_SURROUND},
     *     {@link AudioPresentation#MASTERED_FOR_3D},
     *     {@link AudioPresentation#MASTERED_FOR_HEADPHONE}.
     * @param audioDescriptionAvailable Audio description for the visually impaired.
     * @param spokenSubtitlesAvailable Spoken subtitles for the visually impaired.
     * @param dialogueEnhancementAvailable Dialogue enhancement.
     * @param labels Text label indexed by its locale corresponding to the language code.
     */
    private AudioPresentation(int presentationId,
                             int programId,
                             @NonNull ULocale language,
                             @MasteringIndicationType int masteringIndication,
                             boolean audioDescriptionAvailable,
                             boolean spokenSubtitlesAvailable,
                             boolean dialogueEnhancementAvailable,
                             @NonNull Map<ULocale, String> labels) {
        mPresentationId = presentationId;
        mProgramId = programId;
        mLanguage = language;
        mMasteringIndication = masteringIndication;
        mAudioDescriptionAvailable = audioDescriptionAvailable;
        mSpokenSubtitlesAvailable = spokenSubtitlesAvailable;
        mDialogueEnhancementAvailable = dialogueEnhancementAvailable;
        mLabels = new HashMap<ULocale, String>(labels);
    }

    /**
     * Returns presentation ID used by the framework to select an audio presentation rendered by a
     * decoder. Presentation ID is typically sequential, but does not have to be.
     */
    public int getPresentationId() {
        return mPresentationId;
    }

    /**
     * Returns program ID used by the framework to select an audio presentation rendered by a
     * decoder. Program ID can be used to further uniquely identify the presentation to a decoder.
     */
    public int getProgramId() {
        return mProgramId;
    }

    /**
     * @return a map of available text labels for this presentation. Each label is indexed by its
     * locale corresponding to the language code as specified by ISO 639-2. Either ISO 639-2/B
     * or ISO 639-2/T could be used.
     */
    public Map<Locale, String> getLabels() {
        Map<Locale, String> localeLabels = new HashMap<Locale, String>();
        for (Map.Entry<ULocale, String> entry : mLabels.entrySet()) {
            localeLabels.put(entry.getKey().toLocale(), entry.getValue());
        }
        return localeLabels;
    }

    /**
     * @return the locale corresponding to audio presentation's ISO 639-1/639-2 language code.
     */
    public Locale getLocale() {
        return mLanguage.toLocale();
    }

    private ULocale getULocale() {
        return mLanguage;
    }

    /**
     * @return the mastering indication of the audio presentation.
     * See {@link AudioPresentation#MASTERING_NOT_INDICATED},
     *     {@link AudioPresentation#MASTERED_FOR_STEREO},
     *     {@link AudioPresentation#MASTERED_FOR_SURROUND},
     *     {@link AudioPresentation#MASTERED_FOR_3D},
     *     {@link AudioPresentation#MASTERED_FOR_HEADPHONE}
     */
    @MasteringIndicationType
    public int getMasteringIndication() {
        return mMasteringIndication;
    }

    /**
     * Indicates whether an audio description for the visually impaired is available.
     * @return {@code true} if audio description is available.
     */
    public boolean hasAudioDescription() {
        return mAudioDescriptionAvailable;
    }

    /**
     * Indicates whether spoken subtitles for the visually impaired are available.
     * @return {@code true} if spoken subtitles are available.
     */
    public boolean hasSpokenSubtitles() {
        return mSpokenSubtitlesAvailable;
    }

    /**
     * Indicates whether dialogue enhancement is available.
     * @return {@code true} if dialogue enhancement is available.
     */
    public boolean hasDialogueEnhancement() {
        return mDialogueEnhancementAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AudioPresentation)) {
            return false;
        }
        AudioPresentation obj = (AudioPresentation) o;
        return mPresentationId == obj.getPresentationId()
                && mProgramId == obj.getProgramId()
                && mLanguage == obj.getULocale()
                && mMasteringIndication == obj.getMasteringIndication()
                && mAudioDescriptionAvailable == obj.hasAudioDescription()
                && mSpokenSubtitlesAvailable == obj.hasSpokenSubtitles()
                && mDialogueEnhancementAvailable == obj.hasDialogueEnhancement()
                && mLabels.equals(obj.getLabels());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mPresentationId);
    }

    /**
     * A builder class for creating {@link AudioPresentation} objects.
     */
    public static class Builder {
        private final int mPresentationId;
        private int mProgramId = UNKNOWN_ID;
        private ULocale mLanguage = new ULocale("");
        private int mMasteringIndication = MASTERING_NOT_INDICATED;
        private boolean mAudioDescriptionAvailable = false;
        private boolean mSpokenSubtitlesAvailable = false;
        private boolean mDialogueEnhancementAvailable = false;
        private Map<ULocale, String> mLabels = new HashMap<ULocale, String>();

        /**
         * Create a {@link Builder}. Any field that should be included in the
         * {@link AudioPresentation} must be added.
         *
         * @param presentationId the presentation ID of this audio presentation
         */
        public Builder(int presentationId) {
            mPresentationId = presentationId;
        }
        /**
         * Sets the ProgramId to which this audio presentation refers.
         *
         * @param programId
         */
        public @NonNull Builder setProgramId(int programId) {
            mProgramId = programId;
            return this;
        }
        /**
         * Sets the language information of the audio presentation.
         *
         * @param language code
         */
        public @NonNull Builder setLocale(ULocale language) {
            mLanguage = language;
            return this;
        }

        /**
         * Sets the mastering indication.
         *
         * @param masteringIndication Input to set mastering indication.
         * @throws IllegalArgumentException if the mastering indication is not any of
         * {@link AudioPresentation#MASTERING_NOT_INDICATED},
         * {@link AudioPresentation#MASTERED_FOR_STEREO},
         * {@link AudioPresentation#MASTERED_FOR_SURROUND},
         * {@link AudioPresentation#MASTERED_FOR_3D},
         * and {@link AudioPresentation#MASTERED_FOR_HEADPHONE}
         */
        public @NonNull Builder setMasteringIndication(
                @MasteringIndicationType int masteringIndication) {
            if (masteringIndication != MASTERING_NOT_INDICATED
                    && masteringIndication != MASTERED_FOR_STEREO
                    && masteringIndication != MASTERED_FOR_SURROUND
                    && masteringIndication != MASTERED_FOR_3D
                    && masteringIndication != MASTERED_FOR_HEADPHONE) {
                throw new IllegalArgumentException("Unknown mastering indication: "
                                                        + masteringIndication);
            }
            mMasteringIndication = masteringIndication;
            return this;
        }

        /**
         * Sets locale / text label pairs describing the presentation.
         *
         * @param labels
         */
        public @NonNull Builder setLabels(@NonNull Map<ULocale, String> labels) {
            mLabels = new HashMap<ULocale, String>(labels);
            return this;
        }

        /**
         * Indicate whether the presentation contains audio description for the visually impaired.
         *
         * @param audioDescriptionAvailable
         */
        public @NonNull Builder setHasAudioDescription(boolean audioDescriptionAvailable) {
            mAudioDescriptionAvailable = audioDescriptionAvailable;
            return this;
        }

        /**
         * Indicate whether the presentation contains spoken subtitles for the visually impaired.
         *
         * @param spokenSubtitlesAvailable
         */
        public @NonNull Builder setHasSpokenSubtitles(boolean spokenSubtitlesAvailable) {
            mSpokenSubtitlesAvailable = spokenSubtitlesAvailable;
            return this;
        }

        /**
         * Indicate whether the presentation supports dialogue enhancement.
         *
         * @param dialogueEnhancementAvailable
         */
        public @NonNull Builder setHasDialogueEnhancement(boolean dialogueEnhancementAvailable) {
            mDialogueEnhancementAvailable = dialogueEnhancementAvailable;
            return this;
        }

        /**
         * Creates a {@link AudioPresentation} instance with the specified fields.
         *
         * @return The new {@link AudioPresentation} instance
         */
        public @NonNull AudioPresentation build() {
            return new AudioPresentation(mPresentationId, mProgramId,
                                           mLanguage, mMasteringIndication,
                                           mAudioDescriptionAvailable, mSpokenSubtitlesAvailable,
                                           mDialogueEnhancementAvailable, mLabels);
        }
    }
}

package com.oneliferp.cwu.misc.form;

import io.github.stepio.jgforms.Configuration;
import io.github.stepio.jgforms.Submitter;
import io.github.stepio.jgforms.answer.Builder;
import io.github.stepio.jgforms.exception.NotSubmittedException;

import java.net.MalformedURLException;
import java.net.URL;

public class FormHelper {
    private static final Submitter submitter = new Submitter(new Configuration());

    public static URL build() {
        try {
            return Builder.formKey("1FAIpQLSdUKAoDgLwjjuPWgvs-JoCWvDJLmrPu9Y0rixIwhiUvW2-O2Q")
                    .toUrl();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sumbit(final URL formUrl) {
        try {
            submitter.submitForm(formUrl);
        } catch (NotSubmittedException ex) {
            ex.printStackTrace();
        }
    }
}

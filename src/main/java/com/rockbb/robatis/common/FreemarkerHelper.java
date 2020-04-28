package com.rockbb.robatis.common;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FreemarkerHelper {
    private static Logger LOG = Logger.getLogger(FreemarkerHelper.class.getName());
    private Configuration cfg;
    private String encoding;

    public FreemarkerHelper(String tplPath, String encoding) {
        cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassForTemplateLoading(FreemarkerHelper.class, tplPath);
        cfg.setDefaultEncoding(encoding);
        cfg.setLocalizedLookup(false);
        this.encoding = encoding;
    }

    public Configuration getConfiguration() {
        return cfg;
    }

    public void write(String destPath, String fileName, String template, Object data) {
        try {
            File destFolder = new File(destPath);
            if (!destFolder.isDirectory()) {
                destFolder.mkdirs();
            }
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destPath + fileName, false), "UTF-8"));
            render(writer, template, data);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "IOException:", e);
        }
    }

    private void render(Writer writer, String template, Object root) {

        try {
            Template t = cfg.getTemplate(template);
            // 使用Environment 代替 Template.process, 是为了设置字符型变量的默认输出格式, 以免产生不必要的错误
            Environment env = t.createProcessingEnvironment(root, writer);
            env.setNumberFormat("#");
            env.process();
        } catch (TemplateException e) {
            LOG.log(Level.SEVERE, "TemplateException: ", e);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "IOException: ", e);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception: ", e);
        }
    }
}
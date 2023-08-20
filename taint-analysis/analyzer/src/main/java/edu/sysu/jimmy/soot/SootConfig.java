package edu.sysu.jimmy.soot;

import soot.Scene;
import soot.SootClass;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.options.Options;

public class SootConfig implements IInfoflowConfig {
    @Override
    public void setSootOptions(Options options, InfoflowConfiguration config) {
        Scene.v().addBasicClass("edu.sysu.jimmy.analysis.option.MySink", SootClass.SIGNATURES);
        // explicitly include packages for shorter runtime:
//        List<String> includeList = new LinkedList<>();
//        includeList.add("java.lang.*");
//        includeList.add("java.util.*");
//        includeList.add("java.io.*");
//
//		includeList.add("java.security.*");
//		includeList.add("javax.crypto.*");
//        includeList.add("edu.sysu.cs.jimmy.analysis.option.*");
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
//        Options.v().set_output_format();
//        options.set_include_all(true);
//        includeList.add("java.util.Arrays");
//        options.set_include(includeList);
//        Options.v().set_app(true);ni
//        Options.v().set_on_the_fly(true);
        options.set_output_format(Options.output_format_none);
//        options.set_output_format(Options.output_format_jimple);
        Options.v().set_whole_program(true);
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().set_keep_line_number(true);
        Options.v().set_keep_offset(true);
//        Options.v().set_coffi(true);
        Options.v().set_ignore_classpath_errors(true);
//        Options.v().set_process_dir(new LinkedList<String>(){{add("/home/jimmy/Documents/sourceCode/stateful-taint-tracking/subjectSys/catena/catena-0.0.1-SNAPSHOT.jar");}});
    }
}

package edu.sysu.jimmy.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SubjectSysInfo {
    public static final Logger LOGGER = LoggerFactory.getLogger(SubjectSysInfo.class);
    public static final String PATH_SEP = System.getProperty("path.separator");
    public static final String FILE_SEP = System.getProperty("file.separator");
    public static final String SUBJECT_SYS_HOME = "subjectSys";
    public static final String PREVAYLER_HOME = "prevayler";
    public static final String CATENA_HOME = "catena";
    public static final String SUNFLOW_HOME = "sunflow";
    public static final String H2_HOME = "h2";
    public static final String KANZI_HOME = "kanzi";
    public static final String BATIK_HOME = "batik";
    public static final String DCONVERTER_HOME = "dconverter";
    public static final String BDB_HOME = "berkeleydb";
    public static final String CASSANDRA_HOME = "cassandra";
    public static final String[][] TARGET = {
            {
                    "prevayler",
                    "",
                    SUBJECT_SYS_HOME + FILE_SEP + PREVAYLER_HOME + FILE_SEP
            },
            {
                    "catena",
                    "",
                    SUBJECT_SYS_HOME + FILE_SEP + CATENA_HOME + FILE_SEP
            },
            {
                    "sunflow",
                    "",
                    SUBJECT_SYS_HOME + FILE_SEP + SUNFLOW_HOME + FILE_SEP
            },
            {
                    "h2",
                    "",
                    SUBJECT_SYS_HOME + FILE_SEP + H2_HOME + FILE_SEP
            },
            {
                    "kanzi",
                    "",
                    SUBJECT_SYS_HOME + FILE_SEP + KANZI_HOME + FILE_SEP
            },
            {
                    "batik",
                    "",
                    SUBJECT_SYS_HOME + FILE_SEP + BATIK_HOME + FILE_SEP
            },
            {
                    "dconverter",
                    "",
                    SUBJECT_SYS_HOME + FILE_SEP + DCONVERTER_HOME + FILE_SEP
            },
            {
                    "berkeleydb",
                    "",
                    SUBJECT_SYS_HOME + FILE_SEP + BDB_HOME + FILE_SEP
            },
            {
                    "cassandra",
                    "",
                    SUBJECT_SYS_HOME + FILE_SEP + CASSANDRA_HOME + FILE_SEP
            }
    };
    public static final String[][] CONF_API = {
            {
                    "<org.prevayler.PrevaylerFactory: java.lang.Object prevalentSystem()>|_prevalentSystem",
                    "<org.prevayler.PrevaylerFactory: java.lang.String prevalenceDirectory()>|_prevalenceDirectory",
                    "<org.prevayler.PrevaylerFactory: org.prevayler.Clock clock()>|_clock",
                    "<org.prevayler.PrevaylerFactory: boolean transactionDeepCopyMode()>|_transactionDeepCopyMode",
                    "<org.prevayler.PrevaylerFactory: boolean transientMode()>|_transientMode",
                    "<org.prevayler.PrevaylerFactory: org.prevayler.implementation.snapshot.NullSnapshotManager nullSnapshotManager()>|_nullSnapshotManager",
                    "<org.prevayler.PrevaylerFactory: long journalSizeThreshold()>|_journalSizeThreshold",
                    "<org.prevayler.PrevaylerFactory: long journalAgeThreshold()>|_journalAgeThreshold",
                    "<org.prevayler.PrevaylerFactory: boolean journalDiskSync()>|_journalDiskSync",
                    "<org.prevayler.PrevaylerFactory: org.prevayler.foundation.monitor.Monitor monitor()>|_monitor",
                    "<org.prevayler.PrevaylerFactory: org.prevayler.foundation.serialization.Serializer journalSerializer()>|_journalSerializer",
                    "<org.prevayler.PrevaylerFactory: org.prevayler.implementation.snapshot.GenericSnapshotManager snapshotManager()>|_snapshotSerializers",
            },
            {
                    "<main.java.Catena: java.lang.String get_vId()>|_vId",
                    "<main.java.Catena: main.java.components.hash.HashInterface get_h()>|_h",
                    "<main.java.Catena: main.java.components.hash.HashInterface get_hPrime()>|_hPrime",
                    "<main.java.Catena: main.java.components.gamma.GammaInterface get_gamma()>|_gamma",
                    "<main.java.Catena: main.java.components.graph.GraphInterface get_f()>|_f",
                    "<main.java.Catena: main.java.components.phi.PhiInterface get_phi()>|_phi",
                    "<main.java.Catena: int get_d()>|_d",
                    "<main.java.Catena: int get_n()>|_n",
                    "<main.java.Catena: int get_k()>|_k",
                    "<main.java.Catena: int get_gLow()>|_gLow",
                    "<main.java.Catena: int get_gHigh()>|_gHigh",
                    "<main.java.Catena: int get_lambda()>|_lambda",
                    /*"<monitoring.CatenaMonitoring: boolean _get_hPrime(monitoring.CatenaMonitoring,java.lang.String[])>|_hPrime",
                    "<monitoring.CatenaMonitoring: boolean _get_gamma(monitoring.CatenaMonitoring,java.lang.String[])>|_gamma",
                    "<monitoring.CatenaMonitoring: int _get_f(java.lang.String[])>|_f",
                    "<monitoring.CatenaMonitoring: boolean _get_Phi(monitoring.CatenaMonitoring,java.lang.String[])>|_Phi",
                    "<monitoring.CatenaMonitoring: int _get_gLowHigh(java.lang.String[])>|_gLowHigh",
                    "<monitoring.CatenaMonitoring: int _get_lambda(java.lang.String[])>|_lambda",
                    "<monitoring.CatenaMonitoring: java.lang.String _get_vId(java.lang.String[])>|_vId",
                    "<monitoring.CatenaMonitoring: int _get_d(java.lang.String[])>|_d"*/
            },
            {
//                    "<org.sunflow.Benchmark: int _get_Threads()>|threads",
//                    "<org.sunflow.Benchmark: int _get_Resolution()>|resolution",
//                    "<org.sunflow.Benchmark: int _get_DiffuseDepth()>|diffuseDepth",
//                    "<org.sunflow.Benchmark: int _get_ReflectionDepth()>|reflectionDepth",
//                    "<org.sunflow.Benchmark: int _get_RefractionDepth()>|refractionDepth",
//                    "<org.sunflow.Benchmark: int _get_BucketSize()>|bucketSize",
//                    "<org.sunflow.Benchmark: int _get_Samples()>|samples",
//                    "<org.sunflow.core.ParameterList: java.lang.String getString(java.lang.String,java.lang.String)>",
//                    "<org.sunflow.core.ParameterList: java.lang.String[] getStringArray(java.lang.String,java.lang.String[])>",
                    "<org.sunflow.core.ParameterList: int getInt(java.lang.String,int)>",
//                    "<org.sunflow.core.ParameterList: int[] getIntArray(java.lang.String)>",
//                    "<org.sunflow.core.ParameterList: boolean getBoolean(java.lang.String,boolean)>",
//                    "<org.sunflow.core.ParameterList: float getFloat(java.lang.String,float)>",
//                    "<org.sunflow.core.ParameterList: org.sunflow.image.Color getColor(java.lang.String,org.sunflow.image.Color)>",
//                    "<org.sunflow.core.ParameterList: org.sunflow.math.Point3 getPoint(java.lang.String,org.sunflow.math.Point3)>",
//                    "<org.sunflow.core.ParameterList: org.sunflow.math.Vector3 getVector(java.lang.String,org.sunflow.math.Vector3)>",
//                    "<org.sunflow.core.ParameterList: org.sunflow.math.Point2 getTexCoord(java.lang.String,org.sunflow.math.Point2)>",
//                    "<org.sunflow.core.ParameterList: org.sunflow.math.Matrix4 getMatrix(java.lang.String,org.sunflow.math.Matrix4)>",
//                    "<org.sunflow.core.ParameterList: org.sunflow.core.ParameterList$FloatParameter getFloatArray(java.lang.String)>",
//                    "<org.sunflow.core.ParameterList: org.sunflow.core.ParameterList$FloatParameter getPointArray(java.lang.String)>",
//                    "<org.sunflow.core.ParameterList: org.sunflow.core.ParameterList$FloatParameter getVectorArray(java.lang.String)>",
//                    "<org.sunflow.core.ParameterList: org.sunflow.core.ParameterList$FloatParameter getTexCoordArray(java.lang.String)>",
//                    "<org.sunflow.core.ParameterList: org.sunflow.core.ParameterList$FloatParameter getMatrixArray(java.lang.String)>",
//                    "<org.sunflow.core.ParameterList: org.sunflow.core.ParameterList$FloatParameter getFloatParameter(java.lang.String,org.sunflow.core.ParameterList$ParameterType,org.sunflow.core.ParameterList$Parameter)>"
            },
            {
                    "<org.h2.engine.DbSettings: int getANALYZE_AUTO_setting()>|ANALYZE_AUTO",
                    "<org.h2.engine.DbSettings: int getANALYZE_SAMPLE_setting()>|ANALYZE_SAMPLE",
                    "<org.h2.engine.DbSettings: int getAUTO_COMPACT_FILL_RATE_setting()>|AUTO_COMPACT_FILL_RATE",
                    "<org.h2.engine.DbSettings: boolean getCASE_INSENSITIVE_IDENTIFIERS_setting()>|CASE_INSENSITIVE_IDENTIFIERS",
                    "<org.h2.engine.DbSettings: boolean getDB_CLOSE_ON_EXIT_setting()>|DB_CLOSE_ON_EXIT",
                    "<org.h2.engine.DbSettings: boolean getDEFAULT_CONNECTION_setting()>|DEFAULT_CONNECTION",
                    "<org.h2.engine.DbSettings: java.lang.String getDEFAULT_ESCAPE_setting()>|DEFAULT_ESCAPE",
                    "<org.h2.engine.DbSettings: boolean getDEFRAG_ALWAYS_setting()>|DEFRAG_ALWAYS",
                    "<org.h2.engine.DbSettings: boolean getDROP_RESTRICT()>|DROP_RESTRICT",
                    "<org.h2.engine.DbSettings: int getESTIMATED_FUNCTION_TABLE_ROWS_setting()>|ESTIMATED_FUNCTION_TABLE_ROWS",
                    "<org.h2.engine.DbSettings: int getLOB_TIMEOUT_setting()>|LOB_TIMEOUT",
                    "<org.h2.engine.DbSettings: int getMAX_COMPACT_TIME_setting()>|MAX_COMPACT_TIME",
                    "<org.h2.engine.DbSettings: int getMAX_QUERY_TIMEOUT_setting()>|MAX_QUERY_TIMEOUT",
                    "<org.h2.engine.DbSettings: boolean getOPTIMIZE_DISTINCT_setting()>|OPTIMIZE_DISTINCT",
                    "<org.h2.engine.DbSettings: boolean getOPTIMIZE_EVALUATABLE_SUBQUERIES_setting()>|OPTIMIZE_EVALUATABLE_SUBQUERIES",
                    "<org.h2.engine.DbSettings: boolean getOPTIMIZE_INSERT_FROM_SELECT_setting()>|OPTIMIZE_INSERT_FROM_SELECT",
                    "<org.h2.engine.DbSettings: boolean getOPTIMIZE_IN_LIST_setting()>|OPTIMIZE_IN_LIST",
                    "<org.h2.engine.DbSettings: boolean getOPTIMIZE_IN_SELECT_setting()>|OPTIMIZE_IN_SELECT",
                    "<org.h2.engine.DbSettings: boolean getOPTIMIZE_OR_setting()>|OPTIMIZE_OR",
                    "<org.h2.engine.DbSettings: boolean getOPTIMIZE_TWO_EQUALS()>|OPTIMIZE_TWO_EQUALS",
                    "<org.h2.engine.DbSettings: boolean getOPTIMIZE_SIMPLE_SINGLE_ROW_SUBQUERIES_setting()>|OPTIMIZE_SIMPLE_SINGLE_ROW_SUBQUERIES",
                    "<org.h2.engine.DbSettings: int getQUERY_CACHE_SIZE_setting()>|QUERY_CACHE_SIZE",
                    "<org.h2.engine.DbSettings: boolean getRECOMPILE_ALWAYS_setting()>|RECOMPILE_ALWAYS",
                    "<org.h2.engine.DbSettings: boolean getREUSE_SPACE_setting()>|REUSE_SPACE",
                    "<org.h2.engine.DbSettings: boolean getSHARE_LINKED_CONNECTIONS_setting()>|SHARE_LINKED_CONNECTIONS",
                    "<org.h2.engine.DbSettings: java.lang.String getDEFAULT_TABLE_ENGINE_setting()>|DEFAULT_TABLE_ENGINE",
                    "<org.h2.engine.DbSettings: boolean getMV_STORE_setting()>|MV_STORE",
                    "<org.h2.engine.DbSettings: boolean getCOMPRESS_setting()>|COMPRESS",
                    "<org.h2.engine.DbSettings: boolean getIGNORE_CATALOGS_setting()>|IGNORE_CATALOGS",
                    "<org.h2.engine.DbSettings: boolean getZERO_BASED_ENUMS_setting()>|ZERO_BASED_ENUMS",
            },
            {
//                    "<kanzi.app.Kanzi: char _get_mode(java.util.Map)>|mode",
//                    "<kanzi.app.BlockCompressor$FileCompressTask: java.lang.String _get_inputName()>|inputName",
//                    "<kanzi.app.BlockCompressor$FileCompressTask: java.lang.String _get_outputName()>|outputName",
//                    "<kanzi.app.BlockCompressor: int _get_level(java.util.Map)>|level",
//                    "<kanzi.app.BlockCompressor$FileCompressTask: int _get_verbosity()>|verbose",
//                    "<kanzi.app.BlockCompressor$FileCompressTask: boolean _get_overwrite()>|overwrite",
//                    "<kanzi.io.CompressedOutputStream: java.lang.String _get_codec(java.util.Map)>|entropy",
//                    "<kanzi.io.CompressedOutputStream: java.lang.String _get_transform(java.util.Map)>|transform",
//                    "<kanzi.io.CompressedOutputStream: int _get_jobs(java.util.Map)>|jobs",
//                    "<kanzi.io.CompressedOutputStream: int _get_blockSize(java.util.Map)>|block",
//                    "<kanzi.io.CompressedOutputStream: java.util.concurrent.ExecutorService _get_pool(java.util.Map)>|jobs",
//                    "<kanzi.io.CompressedOutputStream: boolean _get_checksum(java.util.Map)>|checksum",
//                    "<kanzi.io.CompressedOutputStream$EncodingTask: boolean _get_skipBlocks(java.util.Map)>|skipBlocks",
                    "<java.util.Map: java.lang.Object get(java.lang.Object)>",
                    "<java.util.Map: java.lang.Object remove(java.lang.Object)>",
                    "<java.util.Map: java.lang.Object getOrDefault(java.lang.Object,java.lang.Object)>",
            },
            {
                    "<org.apache.batik.transcoder.TranscodingHints: java.lang.Object get(java.lang.Object)>"
//                    "<org.apache.batik.apps.rasterizer.SVGConverter: java.io.File getDst()>|dst",
//                    "<org.apache.batik.apps.rasterizer.SVGConverter: org.apache.batik.apps.rasterizer.DestinationType getDestinationType()>|destinationType",
//                    "<org.apache.batik.transcoder.TranscodingHints: java.lang.Float getKey_WIDTH(java.lang.Object)>|width",
//                    "<org.apache.batik.transcoder.TranscodingHints: java.lang.Float getKey_HEIGHT(java.lang.Object)>|height",
//                    "<org.apache.batik.transcoder.TranscodingHints: java.lang.Float getKey_MAX_WIDTH(java.lang.Object)>|maxWidth",
//                    "<org.apache.batik.transcoder.TranscodingHints: java.lang.Float getKey_MAX_HEIGHT(java.lang.Object)>|maxHeight",
//                    "<org.apache.batik.transcoder.TranscodingHints: java.awt.geom.Rectangle2D getKey_AOI(java.lang.Object)>|area",
//                    "<org.apache.batik.transcoder.TranscodingHints: java.awt.Paint getKey_BACKGROUND_COLOR(java.lang.Object)>|backgroundColor",
//                    "<org.apache.batik.transcoder.SVGAbstractTranscoder$SVGAbstractTranscoderUserAgent: java.lang.String getMedia()>|mediaType",
//                    "<org.apache.batik.transcoder.SVGAbstractTranscoder$SVGAbstractTranscoderUserAgent: java.lang.String getDefaultFontFamily()>|defaultFontFamily",
//                    "<org.apache.batik.transcoder.SVGAbstractTranscoder$SVGAbstractTranscoderUserAgent: java.lang.String getAlternateStyleSheet()>|alternateStylesheet",
//                    "<org.apache.batik.transcoder.SVGAbstractTranscoder$SVGAbstractTranscoderUserAgent: java.lang.String getUserStyleSheetURI()>|userStylesheet",
//                    "<org.apache.batik.transcoder.SVGAbstractTranscoder$SVGAbstractTranscoderUserAgent: java.lang.String getLanguages()>|language",
//                    "<org.apache.batik.transcoder.SVGAbstractTranscoder$SVGAbstractTranscoderUserAgent: float getPixelUnitToMillimeter()>|pixelUnitToMillimeter",
//                    "<org.apache.batik.transcoder.TranscodingHints: java.lang.Float getKey_QUALITY(java.lang.Object)>|quality",
//                    "<org.apache.batik.transcoder.TranscodingHints: java.lang.Integer getKey_INDEXED(java.lang.Object)>|indexed",
//                    "<org.apache.batik.transcoder.TranscodingHints: java.lang.Object getKey_XML_PARSER_VALIDATING(java.lang.Object)>|validate",
//                    "<org.apache.batik.transcoder.SVGAbstractTranscoder$SVGAbstractTranscoderUserAgent: boolean isXMLParserValidating()>|validate1",
//                    "<org.apache.batik.transcoder.TranscodingHints: boolean getKey_EXECUTE_ONLOAD(java.lang.Object)>|executeOnload",
//                    "<org.apache.batik.transcoder.TranscodingHints: java.lang.Float getKey_SNAPSHOT_TIME(java.lang.Object)>|snapshotTime",
//                    "<org.apache.batik.transcoder.SVGAbstractTranscoder$SVGAbstractTranscoderUserAgent: org.apache.batik.bridge.ScriptSecurity getScriptSecurity(java.lang.String,org.apache.batik.util.ParsedURL,org.apache.batik.util.ParsedURL)>|allowedScriptTypes",
//                    "<org.apache.batik.transcoder.SVGAbstractTranscoder$SVGAbstractTranscoderUserAgent: org.apache.batik.bridge.ScriptSecurity getScriptSecurity(java.lang.String,org.apache.batik.util.ParsedURL,org.apache.batik.util.ParsedURL)>|constrainScriptOrigin",
//                    "<org.apache.batik.transcoder.SVGAbstractTranscoder$SVGAbstractTranscoderUserAgent: boolean isAllowExternalResources()>|allowExternalResources",
            },
            {
                    "<at.favre.tools.dconvert.arg.Arguments: java.io.File getSrc()>|src",
                    "<at.favre.tools.dconvert.arg.Arguments: java.io.File getDst()>|dst",
                    "<at.favre.tools.dconvert.arg.Arguments: float getScale()>|scale",
                    "<at.favre.tools.dconvert.arg.Arguments: java.util.Set getPlatform()>|platform",
                    "<at.favre.tools.dconvert.arg.Arguments: at.favre.tools.dconvert.arg.EOutputCompressionMode getOutputCompressionMode()>|outputCompressionMode",
                    "<at.favre.tools.dconvert.arg.Arguments: at.favre.tools.dconvert.arg.EScaleMode getScaleMode()>|scaleMode",
                    "<at.favre.tools.dconvert.arg.Arguments: at.favre.tools.dconvert.arg.EScalingAlgorithm getDownScalingAlgorithm()>|downScalingAlgorithm",
                    "<at.favre.tools.dconvert.arg.Arguments: at.favre.tools.dconvert.arg.EScalingAlgorithm getUpScalingAlgorithm()>|upScalingAlgorithm",
                    "<at.favre.tools.dconvert.arg.Arguments: float getCompressionQuality()>|compressionQuality",
                    "<at.favre.tools.dconvert.arg.Arguments: int getThreadCount()>|threadCount",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isSkipExistingFiles()>|skipExistingFiles",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isSkipUpscaling()>|skipUpscaling",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isVerboseLog()>|verboseLog",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isIncludeAndroidLdpiTvdpi()>|includeAndroidLdpiTvdpi",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isHaltOnError()>|haltOnError",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isCreateMipMapInsteadOfDrawableDir()>|createMipMapInsteadOfDrawableDir",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isEnablePngCrush()>|enablePngCrush",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isEnableMozJpeg()>|enableMozJpeg",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isPostConvertWebp()>|postConvertWebp",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isEnableAntiAliasing()>|enableAntiAliasing",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isDryRun()>|dryRun",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isKeepUnoptimizedFilesPostProcessor()>|keepUnoptimizedFilesPostProcessor",
                    "<at.favre.tools.dconvert.arg.Arguments: at.favre.tools.dconvert.arg.RoundingHandler$Strategy getRoundingHandler()>|roundingHandler",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isIosCreateImagesetFolders()>|iosCreateImagesetFolders",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isGuiAdvancedOptions()>|guiAdvancedOptions",
                    "<at.favre.tools.dconvert.arg.Arguments: boolean isClearDirBeforeConvert()>|clearDirBeforeConvert",
                    "<at.favre.tools.dconvert.arg.Arguments: java.util.List getFilesToProcess()>|filesToProcess",

            },
            {
                    "<com.sleepycat.je.CheckpointConfig: int getKBytes()>|CheckpointConfig-KBytes",
                    "<com.sleepycat.je.CheckpointConfig: int getMinutes()>|CheckpointConfig-Minutes",
                    "<com.sleepycat.je.CheckpointConfig: boolean getForce()>|CheckpointConfig-Force",
                    "<com.sleepycat.je.CheckpointConfig: boolean getMinimizeRecoveryTime()>|CheckpointConfig-MinimizeRecoveryTime",
                    "<com.sleepycat.je.CursorConfig: boolean getReadUncommitted()>|CursorConfig-ReadUncommitted",
                    "<com.sleepycat.je.CursorConfig: boolean getReadCommitted()>|CursorConfig-ReadCommitted",
                    "<com.sleepycat.je.DatabaseConfig: boolean getAllowCreate()>|DatabaseConfig-AllowCreate",
                    "<com.sleepycat.je.DatabaseConfig: boolean getExclusiveCreate()>|DatabaseConfig-ExclusiveCreate",
                    "<com.sleepycat.je.DatabaseConfig: boolean getSortedDuplicates()>|DatabaseConfig-SortedDuplicates",
                    "<com.sleepycat.je.DatabaseConfig: boolean getKeyPrefixing()>|DatabaseConfig-KeyPrefixing",
                    "<com.sleepycat.je.EnvironmentConfig: boolean getAllowCreate()>|EnvironmentConfig-AllowCreate",
                    "<com.sleepycat.je.EnvironmentConfig: long getLockTimeout(java.util.concurrent.TimeUnit)>|EnvironmentConfig-LockTimeout",
                    "<com.sleepycat.je.EnvironmentConfig: long getLockTimeout()>|EnvironmentConfig-LockTimeout",
                    "<com.sleepycat.je.EnvironmentConfig: boolean getReadOnly()>|EnvironmentConfig-ReadOnly",
                    "<com.sleepycat.je.EnvironmentConfig: boolean getTransactional()>|EnvironmentConfig-Transactional",
                    "<com.sleepycat.je.EnvironmentConfig: boolean getLocking()>|EnvironmentConfig-Locking",
                    "<com.sleepycat.je.EnvironmentConfig: long getTxnTimeout(java.util.concurrent.TimeUnit)>|EnvironmentConfig-TxnTimeout",
                    "<com.sleepycat.je.EnvironmentConfig: long getTxnTimeout()>|EnvironmentConfig-TxnTimeout",
                    "<com.sleepycat.je.EnvironmentConfig: boolean getTxnSerializableIsolation()>|EnvironmentConfig-TxnSerializableIsolation",
                    "<com.sleepycat.je.EnvironmentMutableConfig: boolean getTxnNoSync()>|EnvironmentMutableConfig-TxnNoSync",
                    "<com.sleepycat.je.DatabaseConfig: boolean getTransactional()>|DatabaseConfig-Transactional",
                    "<com.sleepycat.je.EnvironmentMutableConfig: boolean getTxnWriteNoSync()>|EnvironmentMutableConfig-TxnWriteNoSync",
                    "<com.sleepycat.je.DatabaseConfig: boolean getReadOnly()>|DatabaseConfig-ReadOnly",
                    "<com.sleepycat.je.EnvironmentConfig: boolean getTxnReadCommitted()>|EnvironmentConfig-TxnReadCommitted",
                    "<com.sleepycat.je.EnvironmentMutableConfig: com.sleepycat.je.Durability getDurability()>|EnvironmentMutableConfig-Durability",
                    "<com.sleepycat.je.DatabaseConfig: int getNodeMaxEntries()>|DatabaseConfig-NodeMaxEntries",
                    "<com.sleepycat.je.DatabaseConfig: int getNodeMaxDupTreeEntries()>|DatabaseConfig-NodeMaxDupTreeEntries",
                    "<com.sleepycat.je.EnvironmentConfig: boolean getSharedCache()>|EnvironmentConfig-SharedCache",
                    "<com.sleepycat.je.EnvironmentMutableConfig: long getCacheSize()>|EnvironmentMutableConfig-CacheSize",
                    "<com.sleepycat.je.EnvironmentConfig: java.lang.String getNodeName()>|EnvironmentConfig-NodeName",
                    "<com.sleepycat.je.EnvironmentMutableConfig: int getCachePercent()>|EnvironmentMutableConfig-CachePercent",
                    "<com.sleepycat.je.EnvironmentMutableConfig: com.sleepycat.je.ExceptionListener getExceptionListener()>|EnvironmentMutableConfig-ExceptionListener",
                    "<com.sleepycat.je.EnvironmentConfig: boolean getCreateUP()>|EnvironmentConfig-CreateUP",
                    "<com.sleepycat.je.EnvironmentMutableConfig: com.sleepycat.je.CacheMode getCacheMode()>|EnvironmentMutableConfig-CacheMode",
                    "<com.sleepycat.je.EnvironmentMutableConfig: com.sleepycat.je.CacheModeStrategy getCacheModeStrategy()>|EnvironmentMutableConfig-CacheModeStrategy",
                    "<com.sleepycat.je.EnvironmentConfig: boolean getCheckpointUP()>|EnvironmentConfig-CheckpointUP",
                    "<com.sleepycat.je.EnvironmentMutableConfig: java.lang.String getConfigParam(java.lang.String)>|EnvironmentMutableConfig-ConfigParam",
                    "<com.sleepycat.je.JoinConfig: boolean getNoSort()>|JoinConfig-NoSort",
                    "<com.sleepycat.je.DatabaseConfig: java.util.Comparator getBtreeComparator()>|DatabaseConfig-BtreeComparator",
                    "<com.sleepycat.je.DatabaseConfig: boolean getBtreeComparatorByClassName()>|DatabaseConfig-BtreeComparatorByClassName",
                    "<com.sleepycat.je.DatabaseConfig: boolean getOverrideBtreeComparator()>|DatabaseConfig-OverrideBtreeComparator",
                    "<com.sleepycat.je.EnvironmentMutableConfig: java.util.Properties getProps()>|EnvironmentMutableConfig-Props",
                    "<com.sleepycat.je.EnvironmentMutableConfig: boolean getLoadPropertyFile()>|EnvironmentMutableConfig-LoadPropertyFile",
                    "<com.sleepycat.je.EnvironmentMutableConfig: int getNumExplicitlySetParams()>|EnvironmentMutableConfig-NumExplicitlySetParams",
                    "<com.sleepycat.je.DatabaseConfig: java.util.Comparator getDuplicateComparator()>|DatabaseConfig-DuplicateComparator",
                    "<com.sleepycat.je.DatabaseConfig: boolean getDuplicateComparatorByClassName()>|DatabaseConfig-DuplicateComparatorByClassName",
                    "<com.sleepycat.je.LogScanConfig: boolean getForwards()>|LogScanConfig-Forwards",
                    "<com.sleepycat.je.DatabaseConfig: boolean getOverrideDuplicateComparator()>|DatabaseConfig-OverrideDuplicateComparator",
                    "<com.sleepycat.je.DatabaseConfig: boolean getTemporary()>|DatabaseConfig-Temporary",
                    "<com.sleepycat.je.DatabaseConfig: boolean getDeferredWrite()>|DatabaseConfig-DeferredWrite",
                    "<com.sleepycat.je.SecondaryConfig: com.sleepycat.je.SecondaryKeyCreator getKeyCreator()>|SecondaryConfig-KeyCreator",
                    "<com.sleepycat.je.PreloadConfig: long getMaxBytes()>|PreloadConfig-MaxBytes",
                    "<com.sleepycat.je.PreloadConfig: long getMaxMillisecs()>|PreloadConfig-MaxMillisecs",
                    "<com.sleepycat.je.PreloadConfig: boolean getLoadLNs()>|PreloadConfig-LoadLNs",
                    "<com.sleepycat.je.SequenceConfig: boolean getAllowCreate()>|SequenceConfig-AllowCreate",
                    "<com.sleepycat.je.SequenceConfig: int getCacheSize()>|SequenceConfig-CacheSize",
                    "<com.sleepycat.je.SequenceConfig: boolean getDecrement()>|SequenceConfig-Decrement",
                    "<com.sleepycat.je.SequenceConfig: boolean getExclusiveCreate()>|SequenceConfig-ExclusiveCreate",
                    "<com.sleepycat.je.SequenceConfig: long getInitialValue()>|SequenceConfig-InitialValue",
                    "<com.sleepycat.je.SequenceConfig: boolean getAutoCommitNoSync()>|SequenceConfig-AutoCommitNoSync",
                    "<com.sleepycat.je.SequenceConfig: long getRangeMin()>|SequenceConfig-RangeMin",
                    "<com.sleepycat.je.SequenceConfig: long getRangeMax()>|SequenceConfig-RangeMax",
                    "<com.sleepycat.je.SequenceConfig: boolean getWrap()>|SequenceConfig-Wrap",
                    "<com.sleepycat.je.StatsConfig: boolean getFast()>|StatsConfig-Fast",
                    "<com.sleepycat.je.StatsConfig: boolean getClear()>|StatsConfig-Clear",
                    "<com.sleepycat.je.StatsConfig: java.io.PrintStream getShowProgressStream()>|StatsConfig-ShowProgressStream",
                    "<com.sleepycat.je.StatsConfig: int getShowProgressInterval()>|StatsConfig-ShowProgressInterval",
                    "<com.sleepycat.je.TransactionConfig: com.sleepycat.je.Durability getDurabilityFromSync()>|TransactionConfig-DurabilityFromSync",
                    "<com.sleepycat.je.SecondaryConfig: com.sleepycat.je.SecondaryMultiKeyCreator getMultiKeyCreator()>|SecondaryConfig-MultiKeyCreator",
                    "<com.sleepycat.je.TransactionConfig: boolean getSync()>|TransactionConfig-Sync",
                    "<com.sleepycat.je.DatabaseConfig: boolean getUseExistingConfig()>|DatabaseConfig-UseExistingConfig",
                    "<com.sleepycat.je.DatabaseConfig: com.sleepycat.je.CacheMode getCacheMode()>|DatabaseConfig-CacheMode",
                    "<com.sleepycat.je.DatabaseConfig: com.sleepycat.je.CacheModeStrategy getCacheModeStrategy()>|DatabaseConfig-CacheModeStrategy",
                    "<com.sleepycat.je.DatabaseConfig: boolean getReplicated()>|DatabaseConfig-Replicated",
                    "<com.sleepycat.je.TransactionConfig: boolean getNoSync()>|TransactionConfig-NoSync",
                    "<com.sleepycat.je.TransactionConfig: boolean getWriteNoSync()>|TransactionConfig-WriteNoSync",
                    "<com.sleepycat.je.TransactionConfig: com.sleepycat.je.Durability getDurability()>|TransactionConfig-Durability",
                    "<com.sleepycat.je.TransactionConfig: com.sleepycat.je.ReplicaConsistencyPolicy getConsistencyPolicy()>|TransactionConfig-ConsistencyPolicy",
                    "<com.sleepycat.je.TransactionConfig: boolean getNoWait()>|TransactionConfig-NoWait",
                    "<com.sleepycat.je.SecondaryConfig: boolean getAllowPopulate()>|SecondaryConfig-AllowPopulate",
                    "<com.sleepycat.je.TransactionConfig: boolean getReadUncommitted()>|TransactionConfig-ReadUncommitted",
                    "<com.sleepycat.je.SecondaryConfig: com.sleepycat.je.Database getForeignKeyDatabase()>|SecondaryConfig-ForeignKeyDatabase",
                    "<com.sleepycat.je.TransactionConfig: boolean getReadCommitted()>|TransactionConfig-ReadCommitted",
                    "<com.sleepycat.je.SecondaryConfig: com.sleepycat.je.ForeignKeyDeleteAction getForeignKeyDeleteAction()>|SecondaryConfig-ForeignKeyDeleteAction",
                    "<com.sleepycat.je.SecondaryConfig: com.sleepycat.je.ForeignKeyNullifier getForeignKeyNullifier()>|SecondaryConfig-ForeignKeyNullifier",
                    "<com.sleepycat.je.SecondaryConfig: com.sleepycat.je.ForeignMultiKeyNullifier getForeignMultiKeyNullifier()>|SecondaryConfig-ForeignMultiKeyNullifier",
                    "<com.sleepycat.je.SecondaryConfig: boolean getImmutableSecondaryKey()>|SecondaryConfig-ImmutableSecondaryKey",
                    "<com.sleepycat.je.VerifyConfig: boolean getPropagateExceptions()>|VerifyConfig-PropagateExceptions",
                    "<com.sleepycat.je.VerifyConfig: boolean getAggressive()>|VerifyConfig-Aggressive",
                    "<com.sleepycat.je.VerifyConfig: boolean getPrintInfo()>|VerifyConfig-PrintInfo",
                    "<com.sleepycat.je.VerifyConfig: java.io.PrintStream getShowProgressStream()>|VerifyConfig-ShowProgressStream",
                    "<com.sleepycat.je.TransactionConfig: boolean getSerializableIsolation()>|TransactionConfig-SerializableIsolation",
                    "<com.sleepycat.je.VerifyConfig: int getShowProgressInterval()>|VerifyConfig-ShowProgressInterval",
                    "<com.sleepycat.je.config.ConfigParam: java.lang.String getName()>|ConfigParam-Name",
                    "<com.sleepycat.je.config.ConfigParam: java.lang.String getDefault()>|ConfigParam-Default",
                    "<com.sleepycat.je.config.ShortConfigParam: java.lang.Short getMin()>|ShortConfigParam-Min",
                    "<com.sleepycat.je.dbi.DbConfigManager: com.sleepycat.je.EnvironmentConfig getEnvironmentConfig()>|DbConfigManager-EnvironmentConfig",
                    "<com.sleepycat.je.dbi.DbConfigManager: java.lang.String get(com.sleepycat.je.config.ConfigParam)>|DbConfigManager-",
                    "<com.sleepycat.je.dbi.DbConfigManager: java.lang.String get(java.lang.String)>|DbConfigManager-",
                    "<com.sleepycat.je.dbi.DbConfigManager: boolean getBoolean(com.sleepycat.je.config.BooleanConfigParam)>|DbConfigManager-Boolean",
                    "<com.sleepycat.je.dbi.DbConfigManager: short getShort(com.sleepycat.je.config.ShortConfigParam)>|DbConfigManager-Short",
                    "<com.sleepycat.je.dbi.DbConfigManager: int getInt(com.sleepycat.je.config.IntConfigParam)>|DbConfigManager-Int",
                    "<com.sleepycat.je.dbi.DbConfigManager: long getLong(com.sleepycat.je.config.LongConfigParam)>|DbConfigManager-Long",
                    "<com.sleepycat.je.dbi.DbConfigManager: int getDuration(com.sleepycat.je.config.DurationConfigParam)>|DbConfigManager-Duration",
                    "<com.sleepycat.je.dbi.DbConfigManager: java.lang.String getConfigParam(java.util.Properties,java.lang.String)>|DbConfigManager-ConfigParam",
                    "<com.sleepycat.je.dbi.DbConfigManager: java.lang.String getVal(java.util.Properties,com.sleepycat.je.config.ConfigParam)>|DbConfigManager-Val",
                    "<com.sleepycat.je.dbi.DbConfigManager: java.lang.String getVal(java.util.Properties,com.sleepycat.je.config.ConfigParam,java.lang.String)>|DbConfigManager-Val",
                    "<com.sleepycat.je.dbi.DbConfigManager: int getIntVal(java.util.Properties,com.sleepycat.je.config.IntConfigParam)>|DbConfigManager-IntVal",
                    "<com.sleepycat.je.dbi.DbConfigManager: boolean getBooleanVal(java.util.Properties,com.sleepycat.je.config.BooleanConfigParam)>|DbConfigManager-BooleanVal",
                    "<com.sleepycat.je.dbi.DbConfigManager: long getDurationVal(java.util.Properties,com.sleepycat.je.config.DurationConfigParam,java.util.concurrent.TimeUnit)>|DbConfigManager-DurationVal",
                    "<com.sleepycat.je.dbi.ReplicatedDatabaseConfig: com.sleepycat.je.DatabaseConfig getReplicaConfig()>|ReplicatedDatabaseConfig-ReplicaConfig",
                    "<com.sleepycat.je.dbi.ReplicatedDatabaseConfig: int getLogSize()>|ReplicatedDatabaseConfig-LogSize",
                    "<com.sleepycat.je.dbi.ReplicatedDatabaseConfig: long getTransactionId()>|ReplicatedDatabaseConfig-TransactionId",
                    "<com.sleepycat.je.rep.NetworkRestoreConfig: boolean getRetainLogFiles()>|NetworkRestoreConfig-RetainLogFiles",
                    "<com.sleepycat.je.rep.NetworkRestoreConfig: java.util.List getLogProviders()>|NetworkRestoreConfig-LogProviders",
                    "<com.sleepycat.je.rep.ReplicationConfig: java.lang.String getGroupName()>|ReplicationConfig-GroupName",
                    "<com.sleepycat.je.rep.ReplicationConfig: boolean getAllowConvert()>|ReplicationConfig-AllowConvert",
                    "<com.sleepycat.je.rep.ReplicationConfig: java.lang.String getNodeName()>|ReplicationConfig-NodeName",
                    "<com.sleepycat.je.rep.ReplicationMutableConfig: boolean getDesignatedPrimary()>|ReplicationMutableConfig-DesignatedPrimary",
                    "<com.sleepycat.je.rep.ReplicationMutableConfig: int getElectableGroupSizeOverride()>|ReplicationMutableConfig-ElectableGroupSizeOverride",
                    "<com.sleepycat.je.rep.ReplicationMutableConfig: int getPriority()>|ReplicationMutableConfig-Priority",
                    "<com.sleepycat.je.rep.ReplicationMutableConfig: java.lang.String getConfigParam(java.lang.String)>|ReplicationMutableConfig-ConfigParam",
                    "<com.sleepycat.je.rep.ReplicationMutableConfig: java.util.Properties getProps()>|ReplicationMutableConfig-Props",
                    "<com.sleepycat.je.rep.ReplicationMutableConfig: boolean getValidateParams()>|ReplicationMutableConfig-ValidateParams",
                    "<com.sleepycat.je.rep.impl.EnumConfigParam: java.lang.Enum getEnumerator(java.lang.String)>|EnumConfigParam-Enumerator",
                    "<com.sleepycat.je.rep.ReplicationConfig: com.sleepycat.je.rep.NodeType getNodeType()>|ReplicationConfig-NodeType",
                    "<com.sleepycat.je.rep.ReplicationConfig: java.lang.String getNodeHostPort()>|ReplicationConfig-NodeHostPort",
                    "<com.sleepycat.je.rep.ReplicationConfig: long getReplicaAckTimeout(java.util.concurrent.TimeUnit)>|ReplicationConfig-ReplicaAckTimeout",
                    "<com.sleepycat.je.rep.ReplicationConfig: long getMaxClockDelta(java.util.concurrent.TimeUnit)>|ReplicationConfig-MaxClockDelta",
                    "<com.sleepycat.je.rep.ReplicationConfig: java.lang.String getHelperHosts()>|ReplicationConfig-HelperHosts",
                    "<com.sleepycat.je.rep.ReplicationConfig: com.sleepycat.je.ReplicaConsistencyPolicy getConsistencyPolicy()>|ReplicationConfig-ConsistencyPolicy",
                    "<com.sleepycat.je.rep.ReplicationConfig: java.util.Set getHelperSockets()>|ReplicationConfig-HelperSockets",
                    "<com.sleepycat.je.rep.ReplicationConfig: java.net.InetSocketAddress getNodeSocketAddress()>|ReplicationConfig-NodeSocketAddress",
                    "<com.sleepycat.je.rep.ReplicationConfig: java.lang.String getNodeHostname()>|ReplicationConfig-NodeHostname",
                    "<com.sleepycat.je.rep.ReplicationConfig: int getNodePort()>|ReplicationConfig-NodePort",
                    "<com.sleepycat.je.rep.util.ldiff.LDiffConfig: int getMaxErrors()>|LDiffConfig-MaxErrors",
                    "<com.sleepycat.je.rep.util.ldiff.LDiffConfig: boolean getDiffAnalysis()>|LDiffConfig-DiffAnalysis",
                    "<com.sleepycat.je.rep.util.ldiff.LDiffConfig: int getBlockSize()>|LDiffConfig-BlockSize",
                    "<com.sleepycat.je.rep.util.ldiff.LDiffConfig: boolean getWaitIfBusy()>|LDiffConfig-WaitIfBusy",
                    "<com.sleepycat.je.rep.util.ldiff.LDiffConfig: int getMaxConnectionAttempts()>|LDiffConfig-MaxConnectionAttempts",
                    "<com.sleepycat.je.rep.util.ldiff.LDiffConfig: int getReconnectDelay()>|LDiffConfig-ReconnectDelay",
                    "<com.sleepycat.je.rep.util.ldiff.LDiffConfig: boolean getVerbose()>|LDiffConfig-Verbose",
                    "<com.sleepycat.persist.StoreConfig: boolean getAllowCreate()>|StoreConfig-AllowCreate",
                    "<com.sleepycat.persist.StoreConfig: boolean getExclusiveCreate()>|StoreConfig-ExclusiveCreate",
                    "<com.sleepycat.persist.StoreConfig: boolean getTransactional()>|StoreConfig-Transactional",
                    "<com.sleepycat.persist.StoreConfig: boolean getReadOnly()>|StoreConfig-ReadOnly",
                    "<com.sleepycat.persist.evolve.EvolveConfig: java.util.Set getClassesToEvolve()>|EvolveConfig-ClassesToEvolve",
                    "<com.sleepycat.persist.evolve.EvolveConfig: com.sleepycat.persist.evolve.EvolveListener getEvolveListener()>|EvolveConfig-EvolveListener",
                    "<com.sleepycat.persist.StoreConfig: boolean getDeferredWrite()>|StoreConfig-DeferredWrite",
                    "<com.sleepycat.persist.StoreConfig: boolean getTemporary()>|StoreConfig-Temporary",
                    "<com.sleepycat.persist.StoreConfig: boolean getSecondaryBulkLoad()>|StoreConfig-SecondaryBulkLoad",
                    "<com.sleepycat.persist.StoreConfig: com.sleepycat.persist.model.EntityModel getModel()>|StoreConfig-Model",
                    "<com.sleepycat.persist.StoreConfig: com.sleepycat.persist.evolve.Mutations getMutations()>|StoreConfig-Mutations",
                    "<com.sleepycat.persist.StoreConfig: com.sleepycat.persist.DatabaseNamer getDatabaseNamer()>|StoreConfig-DatabaseNamer",

            },
            {
                    "<org.apache.cassandra.config.DatabaseDescriptor: java.lang.String getClusterName()>|cluster_name",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getNumTokens()>|num_tokens",
                    "<org.apache.cassandra.config.DatabaseDescriptor: java.lang.String getAllocateTokensForKeyspace()>|allocate_tokens_for_keyspace",
                    "<org.apache.cassandra.config.DatabaseDescriptor: java.lang.Integer getAllocateTokensForLocalRf()>|allocate_tokens_for_local_replication_factor",
                    "<org.apache.cassandra.config.DatabaseDescriptor: java.util.Collection getInitialTokens()>|initial_token",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean hintedHandoffEnabled()>|hinted_handoff_enabled",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getMaxHintWindow()>|max_hint_window_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getHintedHandoffThrottleInKB()>|hinted_handoff_throttle_in_kb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getMaxHintsDeliveryThreads()>|max_hints_delivery_threads",
                    "<org.apache.cassandra.config.DatabaseDescriptor: java.io.File getHintsDirectory()>|hints_directory",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getHintsFlushPeriodInMS()>|hints_flush_period_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getMaxHintsFileSize()>|max_hints_file_size_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.config.ParameterizedClass getHintsCompression()>|hints_compression",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getBatchlogReplayThrottleInKB()>|batchlog_replay_throttle_in_kb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.auth.IAuthenticator getAuthenticator()>|authenticator",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.auth.IAuthorizer getAuthorizer()>|authorizer",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.auth.IRoleManager getRoleManager()>|role_manager",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.auth.INetworkAuthorizer getNetworkAuthorizer()>|network_authorizer",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getRolesValidity()>|roles_validity_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getRolesUpdateInterval()>|roles_update_interval_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getPermissionsValidity()>|permissions_validity_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getPermissionsUpdateInterval()>|permissions_update_interval_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getCredentialsValidity()>|credentials_validity_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getCredentialsUpdateInterval()>|credentials_update_interval_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.dht.IPartitioner getPartitioner()>|partitioner",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean isCDCEnabled()>|cdc_enabled",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.config.Config$DiskFailurePolicy getDiskFailurePolicy()>|disk_failure_policy",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.config.Config$CommitFailurePolicy getCommitFailurePolicy()>|commit_failure_policy",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getPreparedStatementsCacheSizeMB()>|prepared_statements_cache_size_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getKeyCacheSizeInMB()>|key_cache_size_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getKeyCacheSavePeriod()>|key_cache_save_period",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getKeyCacheKeysToSave()>|key_cache_keys_to_save",
                    "<org.apache.cassandra.config.DatabaseDescriptor: java.lang.String getRowCacheClassName()>|row_cache_class_name",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getRowCacheSizeInMB()>|row_cache_size_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getRowCacheSavePeriod()>|row_cache_save_period",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getCounterCacheSizeInMB()>|counter_cache_size_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getCounterCacheSavePeriod()>|counter_cache_save_period",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getCounterCacheKeysToSave()>|counter_cache_keys_to_save",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getCacheLoadTimeout()>|cache_load_timeout_seconds",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.config.Config$CommitLogSync getCommitLogSync()>|commitlog_sync",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getCommitLogSyncPeriod()>|commitlog_sync_period_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getPeriodicCommitLogSyncBlock()>|periodic_commitlog_sync_lag_block_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getCommitLogSegmentSize()>|commitlog_segment_size_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.config.ParameterizedClass getCommitLogCompression()>|commitlog_compression",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.config.Config$FlushCompression getFlushCompression()>|flush_compression",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.locator.SeedProvider getSeedProvider()>|seed_provider",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getConcurrentReaders()>|concurrent_reads",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getConcurrentWriters()>|concurrent_writes",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getConcurrentCounterWriters()>|concurrent_counter_writes",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getConcurrentViewWriters()>|concurrent_materialized_view_writes",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getNetworkingCacheSizeInMB()>|networking_cache_size_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean getFileCacheEnabled()>|file_cache_enabled",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getFileCacheSizeInMB()>|file_cache_size_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.io.util.DiskOptimizationStrategy getDiskOptimizationStrategy()>|disk_optimization_strategy",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getMemtableHeapSpaceInMb()>|memtable_heap_space_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getMemtableOffheapSpaceInMb()>|memtable_offheap_space_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: java.lang.Float getMemtableCleanupThreshold()>|memtable_cleanup_threshold",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.config.Config$MemtableAllocationType getMemtableAllocationType()>|memtable_allocation_type",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getRepairSessionSpaceInMegabytes()>|repair_session_space_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getTotalCommitlogSpaceInMB()>|commitlog_total_space_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getFlushWriters()>|memtable_flush_writers",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getCDCSpaceInMB()>|cdc_total_space_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getCDCDiskCheckInterval()>|cdc_free_space_check_interval_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getIndexSummaryCapacityInMB()>|index_summary_capacity_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getIndexSummaryResizeIntervalInMinutes()>|index_summary_resize_interval_in_minutes",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean getTrickleFsync()>|trickle_fsync",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getTrickleFsyncIntervalInKb()>|trickle_fsync_interval_in_kb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.auth.IInternodeAuthenticator getInternodeAuthenticator()>|internode_authenticator",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean startNativeTransport()>|start_native_transport",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getNativeTransportMaxThreads()>|native_transport_max_threads",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getNativeTransportMaxFrameSize()>|native_transport_max_frame_size_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getNativeTransportMaxConcurrentConnections()>|native_transport_max_concurrent_connections",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getNativeTransportMaxConcurrentConnectionsPerIp()>|native_transport_max_concurrent_connections_per_ip",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean getNativeTransportAllowOlderProtocols()>|native_transport_allow_older_protocols",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long nativeTransportIdleTimeout()>|native_transport_idle_timeout_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean getRpcKeepAlive()>|rpc_keepalive",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getInternodeSocketSendBufferSizeInBytes()>|internode_socket_send_buffer_size_in_bytes",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean isIncrementalBackupsEnabled()>|incremental_backups",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean isSnapshotBeforeCompaction()>|snapshot_before_compaction",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean isAutoSnapshot()>|auto_snapshot",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getSnapshotLinksPerSecond()>|snapshot_links_per_second",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getColumnIndexSize()>|column_index_size_in_kb_0",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getColumnIndexSizeInKB()>|column_index_size_in_kb_1",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getColumnIndexCacheSize()>|column_index_cache_size_in_kb_0",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getColumnIndexCacheSizeInKB()>|column_index_cache_size_in_kb_1",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getCompactionThroughputMbPerSec()>|compaction_throughput_mb_per_sec",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getConcurrentCompactors()>|concurrent_compactors",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getConcurrentValidations()>|concurrent_validations",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getConcurrentViewBuilders()>|concurrent_materialized_view_builders",
//                    "<org.apache.cassandra.config.DatabaseDescriptor: int getCompactionThroughputMbPerSec()>|compaction_throughput_mb_per_sec",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getSSTablePreemptiveOpenIntervalInMB()>|sstable_preemptive_open_interval_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean streamEntireSSTables()>|stream_entire_sstables",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getStreamThroughputOutboundMegabitsPerSec()>|stream_throughput_outbound_megabits_per_sec",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getInterDCStreamThroughputOutboundMegabitsPerSec()>|inter_dc_stream_throughput_outbound_megabits_per_sec",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getReadRpcTimeout(java.util.concurrent.TimeUnit)>|read_request_timeout_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getRangeRpcTimeout(java.util.concurrent.TimeUnit)>|range_request_timeout_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getWriteRpcTimeout(java.util.concurrent.TimeUnit)>|write_request_timeout_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getCounterWriteRpcTimeout(java.util.concurrent.TimeUnit)>|counter_write_request_timeout_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getCasContentionTimeout(java.util.concurrent.TimeUnit)>|cas_contention_timeout_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getTruncateRpcTimeout(java.util.concurrent.TimeUnit)>|truncate_request_timeout_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getRpcTimeout(java.util.concurrent.TimeUnit)>|request_timeout_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getInternodeTcpConnectTimeoutInMS()>|internode_tcp_connect_timeout_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getInternodeTcpUserTimeoutInMS()>|internode_tcp_user_timeout_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getInternodeStreamingTcpUserTimeoutInMS()>|internode_streaming_tcp_user_timeout_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getInternodeApplicationSendQueueCapacityInBytes()>|internode_application_send_queue_capacity_in_bytes",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getInternodeApplicationSendQueueReserveEndpointCapacityInBytes()>|internode_application_send_queue_reserve_endpoint_capacity_in_bytes",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getInternodeApplicationSendQueueReserveGlobalCapacityInBytes()>|internode_application_send_queue_reserve_global_capacity_in_bytes",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getInternodeApplicationReceiveQueueCapacityInBytes()>|internode_application_receive_queue_capacity_in_bytes",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getInternodeApplicationReceiveQueueReserveEndpointCapacityInBytes()>|internode_application_receive_queue_reserve_endpoint_capacity_in_bytes",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getInternodeApplicationReceiveQueueReserveGlobalCapacityInBytes()>|internode_application_receive_queue_reserve_global_capacity_in_bytes",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getSlowQueryTimeout(java.util.concurrent.TimeUnit)>|slow_query_log_timeout_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean hasCrossNodeTimeout()>|cross_node_timeout",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getStreamingKeepAlivePeriod()>|streaming_keep_alive_period_in_secs",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getStreamingConnectionsPerHost()>|streaming_connections_per_host",
                    "<org.apache.cassandra.config.DatabaseDescriptor: double getPhiConvictThreshold()>|phi_convict_threshold",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.locator.IEndpointSnitch getEndpointSnitch()>|endpoint_snitch",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getDynamicUpdateInterval()>|dynamic_snitch_update_interval_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getDynamicResetInterval()>|dynamic_snitch_reset_interval_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: double getDynamicBadnessThreshold()>|dynamic_snitch_badness_threshold",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.config.EncryptionOptions$ServerEncryptionOptions getInternodeMessagingEncyptionOptions()>|server_encryption_options",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.config.EncryptionOptions getNativeProtocolEncryptionOptions()>|client_encryption_options",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.config.Config$InternodeCompression internodeCompression()>|internode_compression",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean getInterDCTcpNoDelay()>|inter_dc_tcp_nodelay",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getTracetypeQueryTTL()>|tracetype_query_ttl",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getTracetypeRepairTTL()>|tracetype_repair_ttl",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean enableUserDefinedFunctions()>|enable_user_defined_functions",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean enableScriptedUserDefinedFunctions()>|enable_scripted_user_defined_functions",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getWindowsTimerInterval()>|windows_timer_interval",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.security.EncryptionContext getEncryptionContext()>|transparent_data_encryption_options",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getTombstoneWarnThreshold()>|tombstone_warn_threshold",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getTombstoneFailureThreshold()>|tombstone_failure_threshold",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getCachedReplicaRowsWarnThreshold()>|cached_rows_warn_threshold",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getCachedReplicaRowsFailThreshold()>|cached_rows_fail_threshold",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getBatchSizeWarnThreshold()>|batch_size_warn_threshold_in_kb_0",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getBatchSizeWarnThresholdInKB()>|batch_size_warn_threshold_in_kb_1",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getBatchSizeFailThreshold()>|batch_size_fail_threshold_in_kb_0",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getBatchSizeFailThresholdInKB()>|batch_size_fail_threshold_in_kb_1",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getUnloggedBatchAcrossPartitionsWarnThreshold()>|unlogged_batch_across_partitions_warn_threshold",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getCompactionLargePartitionWarningThreshold()>|compaction_large_partition_warning_threshold_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getGCLogThreshold()>|gc_log_threshold_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: long getGCWarnThreshold()>|gc_warn_threshold_in_ms",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int getMaxValueSize()>|max_value_size_in_mb",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.db.ConsistencyLevel getIdealConsistencyLevel()>|ideal_consistency_level",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int maxConcurrentAutoUpgradeTasks()>|max_concurrent_automatic_sstable_upgrades",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.audit.AuditLogOptions getAuditLoggingOptions()>|audit_logging_options",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.fql.FullQueryLoggerOptions getFullQueryLogOptions()>|full_query_logging_options",
                    "<org.apache.cassandra.config.DatabaseDescriptor: org.apache.cassandra.config.Config$CorruptedTombstoneStrategy getCorruptedTombstoneStrategy()>|corrupted_tombstone_strategy",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean diagnosticEventsEnabled()>|diagnostic_events_enabled",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean useNativeTransportLegacyFlusher()>|native_transport_flush_in_batches_legacy",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean getRepairedDataTrackingForRangeReadsEnabled()>|repaired_data_tracking_for_range_reads_enabled",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean reportUnconfirmedRepairedDataMismatches()>|report_unconfirmed_repaired_data_mismatches",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int tableCountWarnThreshold()>|table_count_warn_threshold",
                    "<org.apache.cassandra.config.DatabaseDescriptor: int keyspaceCountWarnThreshold()>|keyspace_count_warn_threshold",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean getEnableMaterializedViews()>|enable_materialized_views",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean getEnableSASIIndexes()>|enable_sasi_indexes",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean isTransientReplicationEnabled()>|enable_transient_replication",
                    "<org.apache.cassandra.config.DatabaseDescriptor: boolean enableDropCompactStorage()>|enable_drop_compact_storage",
            }
    };
    public static final String[][] PKG = {
            {
                    "org",
//                    "org.prevayler",
//                    "junkyard",
            },
            {
                    "main.java",
                    "monitoring"
            },
            {
                    "org.sunflow"
            },
            {
                    "org.h2",
                    "profiling"
            },
            {
                    "kanzi"
            },
            {
                    "org.apache.batik"
            },
            {
                    "at.favre.tools.dconvert"
            },
            {
                    "com.sleepycat"
            },
            {
                    "org.apache.cassandra"
            }
    };
    public static final String[][] ARGS = {
            {},
            {},
            {
                    "bucket.size",
                    "threads",
                    "depths.diffuse",
                    "depths.reflection",
                    "depths.refraction",
                    "gi.igi.samples",
            },
            {},
            {
                    "mode",
                    "inputName",
                    "outputName",
                    "verbosity",
                    "entropy",
                    "overwrite",
                    "level",
                    "codec",
                    "transform",
                    "jobs",
                    "blockSize",
                    "pool",
                    "checksum",
                    "skipBlocks",
                    "from",
                    "to",
                    "size",
            },
            {
                    "WIDTH",
                    "HEIGHT",
                    "MAX_WIDTH",
                    "MAX_HEIGHT",
                    "AOI",
                    "BACKGROUND_COLOR",
                    "MEDIA",
                    "ALTERNATE_STYLESHEET",
                    "USER_STYLESHEET_URI",
                    "DEFAULT_FONT_FAMILY",
                    "LANGUAGE",
                    "QUALITY",
                    "INDEXED",
                    "PIXEL_UNIT_TO_MILLIMETER",
                    "XML_PARSER_VALIDATING",
                    "EXECUTE_ONLOAD",
                    "SNAPSHOT_TIME",

                    "CONSTRAIN_SCRIPT_ORIGIN",
                    "ALLOWED_SCRIPT_TYPES",

            },
            {},
            {},
            {}

    };

    public static String getAppClassPath(String target[]) {
        String classpath = "";
        if ("prevayler".compareTo(target[0]) == 0)
            classpath += dealClassPath(target[2]);
        else if ("catena".compareTo(target[0]) == 0)
            classpath += dealClassPath(target[2]);
        else if ("sunflow".compareTo(target[0]) == 0)
            classpath += dealClassPath(target[2]);
        else if ("h2".compareTo(target[0]) == 0)
            classpath += dealClassPath(target[2]);
        else if ("kanzi".compareTo(target[0]) == 0)
            classpath += dealClassPath(target[2]);
        else if ("batik".compareTo(target[0]) == 0)
            classpath += dealClassPath(target[2]);
        else if ("dconverter".compareTo(target[0]) == 0)
            classpath += dealClassPath(target[2]);
        else if ("berkeleydb".compareTo(target[0]) == 0) {
            classpath += dealClassPath(target[2]);
        } else if ("cassandra".compareTo(target[0]) == 0) {
            classpath += dealClassPath(target[2]);
        }
        return classpath;
    }

    public static List<String> getSourcePath(String target[]) {
        if ("prevayler".compareTo(target[0]) == 0) {
            return dealPureJars(target[2]);
        } else if ("catena".compareTo(target[0]) == 0) {
            return dealPureJars(target[2]);
        } else if ("sunflow".compareTo(target[0]) == 0) {
            return dealPureJars(target[2]);
        } else if ("h2".compareTo(target[0]) == 0) {
            return dealPureJars(target[2]);
        } else if ("kanzi".compareTo(target[0]) == 0) {
            return dealPureJars(target[2]);
        } else if ("batik".compareTo(target[0]) == 0) {
            return dealPureJars(target[2]);
        } else if ("dconverter".compareTo(target[0]) == 0)
            return dealPureJars(target[2]);
        else if ("berkeleydb".compareTo(target[0]) == 0) {
            return dealPureJars(target[2]);
        } else if ("cassandra".compareTo(target[0]) == 0) {
            return dealPureJars(target[2]);
        }
        return null;
    }

    public static String dealClassPath(String dirPath) {
        StringBuffer cp = new StringBuffer("");
        File dir = new File(dirPath);
        if (dir.isDirectory() == false) {
            LOGGER.error(dirPath + " should be a directory path.");
        }
        File[] files = dir.listFiles();
        if (files.length > 0)
            for (File file : files) {
                if (file.getName().endsWith(".jar")) {
                    cp.append(PATH_SEP);
                    cp.append(file.getAbsolutePath());
                }
            }
        return cp.toString();
    }

    public static List<String> dealPureJars(String srcDirPath) {
        LinkedList<String> jars = new LinkedList<>();
        jars.clear();
        StringBuffer sp = new StringBuffer("");
        File dir = new File(srcDirPath);
        if (dir.isDirectory() == false) {
            LOGGER.error(srcDirPath + " should be a directory path.");
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith("jar")) {
                jars.add(file.getAbsolutePath());
            }
        }
        return jars;
    }

    public static int getOrder(String targetName) {
        int i = -1;
        for (int j = 0; j < TARGET.length; j++) {
            if (TARGET[j][0].compareTo(targetName) == 0) {
                i = j;
                break;
            }
        }
        return i;
    }

    public static List<String> getEntryPoint(String[] target) throws IOException {
        ArrayList<String> entryPoints = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(target[2] + target[0] + "EntryPointList.txt")));
        String method = null;
        while ((method = bufferedReader.readLine()) != null) {
            entryPoints.add(method);
        }
        bufferedReader.close();
        return entryPoints;
    }

    public static String[] prepareArgs(String sys, String[] initArgs) {
        File f = new File(".");
        String classPath = ".";
        List<String> srcPaths = new LinkedList<>();
        File testSrc1 = new File(f, "analyzer" + File.separator + "target" + File.separator + "classes");
        switch (sys) {
            case "prevayler":
//                classPath = classPath + File.pathSeparator + SubjectSysInfo.getAppClassPath(SubjectSysInfo.TARGET[0]);
                srcPaths.addAll(SubjectSysInfo.getSourcePath(SubjectSysInfo.TARGET[0]));
                break;
            case "catena":
                srcPaths.addAll(SubjectSysInfo.getSourcePath(SubjectSysInfo.TARGET[1]));
                break;
            case "sunflow":
//                classPath = classPath + File.pathSeparator + SubjectSysInfo.getAppClassPath(SubjectSysInfo.TARGET[2]);
                srcPaths.addAll(SubjectSysInfo.getSourcePath(SubjectSysInfo.TARGET[2]));
                break;
            case "h2":
                srcPaths.addAll(SubjectSysInfo.getSourcePath(SubjectSysInfo.TARGET[3]));
                break;
            case "kanzi":
                srcPaths.addAll(SubjectSysInfo.getSourcePath(SubjectSysInfo.TARGET[4]));
                break;
            case "batik":
                srcPaths.addAll(SubjectSysInfo.getSourcePath(SubjectSysInfo.TARGET[5]));
                break;
            case "dconverter":
                srcPaths.addAll(SubjectSysInfo.getSourcePath(SubjectSysInfo.TARGET[6]));
                break;
            case "bdb":
//                srcPaths.addAll(SubjectSysInfo.getSourcePath(SubjectSysInfo.TARGET[7]));
                File bdbbase = new File(f, "subjectSys" + File.separator + "berkeleydb" + File.separator + "build");

                File bdbclasses = new File(bdbbase, "classes");
                File bdb_test_classes = new File(bdbbase, "test" + File.separator + "classes");
                File bdb_tmp_classes = new File(bdbbase, "test" + File.separator + "classes");
                File bdb_standalone_classes = new File(bdbbase, "test" + File.separator + "standalone" + File.separator + "classes");
                File bdb_testclassloader_classes = new File(bdbbase, "test" + File.separator + "testclassloader");
                srcPaths.add(bdbclasses.getAbsolutePath());
                srcPaths.add(bdb_test_classes.getAbsolutePath());
                srcPaths.add(bdb_tmp_classes.getAbsolutePath());
                srcPaths.add(bdb_standalone_classes.getAbsolutePath());
                srcPaths.add(bdb_testclassloader_classes.getAbsolutePath());
                break;
            case "cassandra":
                srcPaths.addAll(SubjectSysInfo.getSourcePath(SubjectSysInfo.TARGET[8]));
                break;
            default:

                File testSrc2 = new File(f, "analyzer" + File.separator + "target" + File.separator + "test-classes");
                classPath = classPath + File.pathSeparator + testSrc1.getAbsolutePath() + File.pathSeparator + testSrc2.getAbsolutePath();

                srcPaths.add(testSrc2.getAbsolutePath());
        }
        srcPaths.add(testSrc1.getAbsolutePath());

        initArgs[1] = classPath;
        String[] sootArgs = new String[initArgs.length + 2 * srcPaths.size() + 2];
        for (int i = 0; i < initArgs.length; i++) {
            sootArgs[i] = initArgs[i];
        }
        for (int i = 0; i < srcPaths.size(); i++) {
            sootArgs[initArgs.length + 2 * i] = "-process-dir";
            sootArgs[initArgs.length + 2 * i + 1] = srcPaths.get(i);
        }
        sootArgs[sootArgs.length - 2] = "-main-class";
        sootArgs[sootArgs.length - 1] = getMainClass(sys);
        return sootArgs;
    }

    public static String getMainClass(String sysName) {
        String mainClass = null;
        switch (sysName) {
            case "kanzi":
                mainClass = "kanzi.app.Kanzi";
                break;
            case "catena":
                mainClass = "monitoring.CatenaMonitoring";
                break;
            case "prevayler":
                mainClass = "org.prevayler.demos.scalability.ProfilingEntry";
                break;
            case "batik":
                mainClass = "org.apache.batik.apps.rasterizer.Main";
                break;
            case "h2":
                mainClass ="profiling.Profiler";
                break;
        }
        return mainClass;
    }
}

package io.system.counter.manager;

import io.system.counter.model.RuleCounter;
import io.system.counter.reader.FactoryReader;
import io.system.counter.reader.data.DataReader;
import io.system.counter.reader.rule.RuleReader;
import io.system.counter.writer.FactoryWriter;
import io.system.counter.writer.Writer;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

public final class FileProcessor {

    private FileProcessor(){}

    /**
     * Загружает файлы и проверяет согласно правилам загружанные данные
     * @param ruleFile - Имя файла с правилами
     * @param inputFile - Имя файла с данными
     * @return Список обработанных правил
     * @throws FileNotFoundException - Исключения, когда не возможно было определить тип загружаемых файлов
     */
    public static List<RuleCounter> checkRules(final String ruleFile,
                                               final String inputFile) throws FileNotFoundException {
        final Optional<FactoryReader> typeRule = FactoryReader.get(ruleFile);
        final Optional<FactoryReader> typeData = FactoryReader.get(inputFile);
        if(!typeRule.isPresent()) {
            throw new FileNotFoundException("Not understand format rule file " + ruleFile);
        }else if(!typeData.isPresent()){
            throw new FileNotFoundException("Not understand format data file " + inputFile);
        }
        final RuleReader<?> ruleReader = typeRule.get().getRuleReader(ruleFile);
        ruleReader.load();
        final DataReader<?> dataReader = typeData.get().getDataReader(inputFile, ruleReader.getRules());
        dataReader.load();
        return dataReader.getRules();
    }

    /**
     * Загружает файлы и проверяет согласно правилам загруженные данные. Выводит информацию в указанный путь
     * @param ruleFile - Имя файла с правилами
     * @param inputFile - Имя файла с данными
     * @param outPutFile - Имя выходного файла, либо пустая для вывода в консоль
     * @throws Exception - Исключения, когда не возможно было определить тип загружаемых файлов или записать информацию в файл
     */
    public static void checkRulesAndWrite(final String ruleFile,
                                           final String inputFile,
                                           final String outPutFile) throws Exception{
        final List<RuleCounter> ruleCounters = checkRules(ruleFile, inputFile);
        final FactoryWriter typeWrite = FactoryWriter.get(outPutFile);
        try(final Writer writer = typeWrite.getWriter(outPutFile, ruleCounters)){
            writer.output();
        }catch (Exception e){
            throw new Exception(e);
        }
    }
}

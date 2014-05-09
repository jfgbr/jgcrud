package com.jgalante.jgcrud.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatHelper {

	/**
	 * Converte {@link String} em {@link CNPJ}
	 * 
	 * @param value
	 *            CNPJ no formato de texto
	 * @return {@link CNPJ}
	 */
	//TODO: verify if it is needed and the best place
//	public static CNPJ converteStringToCnpj(String value) {
//
//		if (value == null || value.isEmpty()) {
//			return null;
//		} else {
//			CNPJ cnpj = new CNPJ();
//			String cnpjTxt = retiraMascaraCnpj(value);
//			cnpj.setNumero(cnpjTxt);
//			return cnpj;
//		}
//	}

	/**
	 * Converte {@link CNPJ} em {@link String}
	 * 
	 * @param cnpj
	 *            CNPJ
	 * @return {@link String}
	 */
	//TODO: verify if it is needed and the best place
//	public static String converteCnpjToString(CNPJ cnpj) {
//		String cnpjCompleto;
//
//		try {
//			cnpjCompleto = cnpj.getNumero();
//
//			cnpjCompleto = cnpjCompleto.replaceAll("^(\\d{2})(\\d)", "$1.$2");
//			cnpjCompleto = cnpjCompleto.replaceAll("^(\\d{2})\\.(\\d{3})(\\d)", "$1.$2.$3");
//			cnpjCompleto = cnpjCompleto.replaceAll("\\.(\\d{3})(\\d)", ".$1/$2");
//			cnpjCompleto = cnpjCompleto.replaceAll("(\\d{4})(\\d)", "$1-$2");
//
//		} catch (Exception e) {
//			cnpjCompleto = "";
//		}
//
//		return cnpjCompleto;
//	}

	public static String raizCNPJ(String cnpj) {
		String raiz;
		try {
			String cnpjTxt = retiraMascaraCnpj(cnpj);

			raiz = cnpjTxt.substring(0, 8);

		} catch (Exception e) {
			raiz = null;
		}
		return raiz;
	}

	public static String estabelecimentoCNPJ(String cnpj) {
		String estabelecimento;
		try {
			String cnpjTxt = retiraMascaraCnpj(cnpj);

			estabelecimento = cnpjTxt.substring(8, 12);

		} catch (Exception e) {
			estabelecimento = null;
		}
		return estabelecimento;
	}

	public static String digitoCNPJ(String cnpj) {
		String digito;
		try {
			String cnpjTxt = retiraMascaraCnpj(cnpj);

			digito = cnpjTxt.substring(12);

		} catch (Exception e) {
			digito = null;
		}
		return digito;
	}

	/**
	 *  Retira a máscara de um CNPJ ou CPF no formato de texto
	 *  
	 * @param valor
	 * @return
	 */
	public static String retiraMascara(String valor){
		
		if (valor == null) {
			return "";
		}
		
		return valor.replace(".", "").replace("-", "").replace("/", "").replace("_", "");
	}
	
	public static String retiraMascaraCpf(String cpf) {
		
		return retiraMascara(cpf);
		
	}
	
	/**
	 * Retira a máscara de um CNPJ no formato de texto
	 * 
	 * @param cnpj
	 *            Formatado
	 * @return {@link String}
	 */
	public static String retiraMascaraCnpj(String cnpj) {
		
		return retiraMascara(cnpj);
		
	}

	/**
	 * Formata um número de CPF
	 * 
	 * @param cpf
	 * @return cpf formatado, com '.' e '-'
	 */
	//TODO: verify if it is needed and the best place
//	public static String formataCpf(String cpf) {
//
//		// Maior
//		if (cpf.length() > 11) {
//			return cpf;
//		}
//
//		// Preenche zeros até o tamanho correto
//		final String auxCpf = StringUtils.leftPad(cpf, 11, "0");
//
//		return formata(auxCpf, "###.###.###-##");
//	}

	/**
	 * Realiza a formatacao do valor de acordo com a mascara enviada
	 * 
	 * @param valor
	 * @param mascara
	 * @return {@link String}
	 */
	public static String formata(String valor, String mascara) {
		String dado = "";
		// remove caracteres nao numericos
		for (int i = 0; i < valor.length(); i++) {
			char c = valor.charAt(i);
			if (Character.isDigit(c)) {
				dado += c;
			}
		}

		int indMascara = mascara.length();
		int indCampo = dado.length();

		for (; indCampo > 0 && indMascara > 0;) {
			if (mascara.charAt(--indMascara) == '#') {
				indCampo--;
			}
		}

		String saida = "";
		for (; indMascara < mascara.length(); indMascara++) {
			saida += ((mascara.charAt(indMascara) == '#') ? dado.charAt(indCampo++) : mascara.charAt(indMascara));
		}
		return saida;
	}

	public static List<String> extrairNumeroDIAdicao(String texto) {
        Pattern p = Pattern.compile("(\\d{10}/\\d{3})|\\d{13}");
        Matcher m = p.matcher(texto);
        List<String> resultado = new ArrayList<String>();
        
        while (m.find()) {
        	String group = m.group();
        	if(group.indexOf('/') == -1) {
        		group = group.substring(0,10) + "/" + group.substring(10);
        	}
        	resultado.add(group);
        }
        
        return resultado;
	}
	
	/**
	 * Verifica se uma data está dentro do período informado
	 * 
	 * @param dataInicialPeriodo data inicial do período
	 * @param dataFinalPeriodo data final do período
	 * @param data data a ser comparada
	 * @return Verdadeiro se estiver no período
	 */
	public static boolean validaDataNoPeriodo(Date dataInicialPeriodo, Date dataFinalPeriodo, Date data) {
		
		Calendar dataIncial = Calendar.getInstance();
		dataIncial.setTimeInMillis(dataInicialPeriodo.getTime());
		
		Calendar dataFinal = Calendar.getInstance();
		dataFinal.setTimeInMillis(dataFinalPeriodo.getTime());
		
		Calendar dataValidada = Calendar.getInstance();
		dataValidada.setTimeInMillis(data.getTime());
		
		return ((dataIncial.compareTo(dataValidada) <= 0) && (dataFinal.compareTo(dataValidada) >= 0));
	}
	
	
	/**
	 * Verifica se uma data é válida. Uma data é válida caso ela seja maior ou igual a
	 * 01/01/1900 e menor ou igual a data atual.
	 * 
	 * @param data data a ser verificada
	 * @return verdadeiro caso a data seja válida e falso caso contrário ou se a data for nula
	 */
	public static boolean validaData(Date data) {
		if (data == null) {
			return false;
		}
		
		Calendar d = Calendar.getInstance();
		d.setTimeInMillis(data.getTime());
		Calendar base = new GregorianCalendar(1900, Calendar.JANUARY, 01);
		Calendar atual = Calendar.getInstance();
		
		return validaDataNoPeriodo(base.getTime(),atual.getTime(),data);
	}
	
	/**
	 * Retorna a data atual sem a hora
	 * 
	 * @return data atual
	 */
	public static Date dataAtual() {
		Calendar atual = Calendar.getInstance();
		return atual.getTime();
	}
	
	public static void main(String[] args) {
		System.out.println(extrairNumeroDIAdicao("4569874569/879,3214567896/546,7896541236547"));
	}
}

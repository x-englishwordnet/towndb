/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.wndb.out;

import org.oewntk.model.CoreModel;
import org.oewntk.model.Lex;
import org.oewntk.model.Sense;
import org.oewntk.model.Synset;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Class that produces the synset line from model and synsetId
 *
 * @author Bernard Bou
 */
public class LineProducer implements BiFunction<CoreModel, String, String>
{
	private final int flags;

	public LineProducer(final int flags)
	{
		this.flags = flags;
	}

	@Override
	public String apply(final CoreModel model, final String synsetId)
	{
		// Compute synset offsets
		Map<String, Long> offsets = new OffsetFactory(model.lexesByLemma, model.synsetsById, model.sensesById, flags).compute();

		// Get synset
		Synset synset = model.synsetsById.get(synsetId);
		long offset = offsets.get(synsetId);
		if (!offsets.containsValue(offset))
		{
			throw new IllegalArgumentException(String.format("%d is not a valid offset", offset));
		}

		// Produce line
		return data(synset, offset, model.lexesByLemma, model.synsetsById, model.sensesById, offsets, flags);
	}

	/**
	 * Grind data for this synset
	 *
	 * @param synset       synset
	 * @param offset       offset
	 * @param lexesByLemma lexes mapped by lemma
	 * @param synsetsById  synset elements mapped by id
	 * @param sensesById   sense elements mapped by id
	 * @param offsets      offsets mapped by synsetId
	 * @return line
	 */
	public static String data(Synset synset, long offset, //
			Map<String, List<Lex>> lexesByLemma, //
			Map<String, Synset> synsetsById, //
			Map<String, Sense> sensesById, //
			Map<String, Long> offsets, int flags)
	{
		// Data
		DataGrinder factory = new DataGrinder(lexesByLemma, synsetsById, sensesById, offsets, flags);
		return factory.getData(synset, offset);
	}
}
/**
 * eobjects.org DataCleaner
 * Copyright (C) 2010 eobjects.org
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.eobjects.datacleaner.widgets.properties;

import org.eobjects.analyzer.descriptors.ConfiguredPropertyDescriptor;
import org.eobjects.analyzer.job.builder.AbstractBeanJobBuilder;
import org.eobjects.analyzer.job.builder.AnalysisJobBuilder;
import org.eobjects.metamodel.util.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimalistic abstract implementation of the {@link PropertyWidget} interface.
 * 
 * @author Kasper Sørensen
 *
 * @param <E>
 */
public abstract class MinimalPropertyWidget<E> implements PropertyWidget<E> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractPropertyWidget.class);

	private final AbstractBeanJobBuilder<?, ?, ?> _beanJobBuilder;
	private final ConfiguredPropertyDescriptor _propertyDescriptor;

	public MinimalPropertyWidget(AbstractBeanJobBuilder<?, ?, ?> beanJobBuilder,
			ConfiguredPropertyDescriptor propertyDescriptor) {
		_beanJobBuilder = beanJobBuilder;
		_propertyDescriptor = propertyDescriptor;
	}

	@Override
	public void initialize(E value) {
	};

	@Override
	public final ConfiguredPropertyDescriptor getPropertyDescriptor() {
		return _propertyDescriptor;
	}

	public final AbstractBeanJobBuilder<?, ?, ?> getBeanJobBuilder() {
		return _beanJobBuilder;
	}

	@Override
	public boolean isSet() {
		return getValue() != null;
	}

	public final void fireValueChanged() {
		final E value = getValue();
		fireValueChanged(value);
	}

	protected final AnalysisJobBuilder getAnalysisJobBuilder() {
		return _beanJobBuilder.getAnalysisJobBuilder();
	}

	@Override
	public void onValueTouched(E value) {
		E existingValue = getValue();
		if (EqualsBuilder.equals(value, existingValue)) {
			return;
		}
		setValue(value);
	}

	/**
	 * Gets the current value of the property in the job builder.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected E getCurrentValue() {
		return (E) getBeanJobBuilder().getConfiguredProperty(getPropertyDescriptor());
	}

	protected abstract void setValue(E value);

	protected final void fireValueChanged(Object newValue) {
		try {
			_beanJobBuilder.setConfiguredProperty(_propertyDescriptor, newValue);
		} catch (Exception e) {
			// an exception will be thrown here if setting an invalid property
			// value (which may just be work in progress, so we don't make a
			// fuzz about it)
			if (logger.isWarnEnabled()) {
				logger.warn("Exception thrown when setting configured property " + _propertyDescriptor, e);
			}
		}
	}
}
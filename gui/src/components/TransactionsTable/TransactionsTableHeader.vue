<template>
  <thead>
    <tr>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th colspan="4" class="border-l-2 border-base-content">Debtor</th>
      <th colspan="4" class="border-x-2 border-base-content">Creditor</th>
      <th></th>
      <th></th>
      <th></th>
    </tr>
    <tr>
      <th>
        Type
        <slot name="type-btn" />
      </th>
      <th>
        Dir
        <slot name="dir-btn" />
      </th>
      <th>
        Status
        <slot name="status-btn" />
      </th>
      <th>
        UETR
        <slot name="uetr-btn" />
      </th>
      <th class="border-l-2 border-base-content">
        BIC
        <slot name="debtor-bic-btn" />
      </th>
      <th>
        Client
        <slot name="debtor-client-btn" />
      </th>
      <th>
        Amount
        <slot name="debtor-amount-btn" />
      </th>
      <th>
        Currency
        <slot name="debtor-currency-btn" />
      </th>
      <th class="border-l-2 border-base-content">
        BIC
        <slot name="creditor-bic-btn" />
      </th>
      <th>
        Client
        <slot name="creditor-client-btn" />
      </th>
      <th>
        Amount
        <slot name="creditor-amount-btn" />
      </th>
      <th class="border-r-2 border-base-content">
        Currency
        <slot name="creditor-currency-btn" />
      </th>
      <th>
        Created
        <slot name="created-btn" />
      </th>
      <th>
        Updated
        <slot name="updated-btn" />
      </th>
      <th>Actions</th>
    </tr>
  </thead>
</template>

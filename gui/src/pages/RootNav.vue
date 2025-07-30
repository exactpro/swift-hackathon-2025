<script setup lang="ts">
import { useHead } from '@unhead/vue'
import { RouterLink } from 'vue-router'
useHead({
  title: 'About Us'
})
</script>

<template>
  <article class="prose dark:prose-invert max-w-4xl mx-auto p-6">
    <h1>Coincento Solution Prototype</h1>

    <p>
      <strong>Coincento</strong> is a DLT-based platform with a mission to harmonise fiat currencies and digital coins
      into one well-functioning ecosystem, enabling regulation-compliant real-time cross-border payments and settlement.
    </p>

    <p>
      While addressing the objectives of real-time accuracy, transparency, interoperability and operational efficiency,
      <strong>Coincento</strong> enables seamless integration with the ISO 20022-based banking infrastructure,
      capitalising on and requiring minimal modifications to the existing banking platforms.
    </p>

    <p>
      The main components of the platform are: Bank’s Client App for its clients, Core Banking Application (please see
      links in the main Root Navigation panel) and an EVM blockchain with customised smart contracts. This architecture
      allows clients and banking operators to follow their workflows, including management of the transactions and their
      reporting. The creation and execution of smart contracts within the Ethereum Mainnet Network is realised via
      <strong>Coincento’s smart contract</strong> - an ERC-1155 smart token that is flexible and customised to support
      multiple types of base tokens.
    </p>

    <p>
      The solution is entity-, bank-, and technology-agnostic. It is integrated with a transactions dashboard for
      further analysis, operational flow management and analytics. It enables transaction visibility across participants
      and end-to-end traceability for the use case flow and further process mapping, reconciliation, streamlined
      internal audits and regulatory reporting.
    </p>

    <h2>Application Navigation</h2>

    <h3>Bank A</h3>
    <ul>
      <li>
        <strong>Client Banking Interface:</strong>
        <ul>
          <li><RouterLink to="/banka">Client Dashboard</RouterLink></li>
          <li><RouterLink to="/banka/transfer">Transfer Form</RouterLink></li>
        </ul>
      </li>
      <li>
        <strong>Administrative Interface:</strong>
        <ul>
          <li><RouterLink to="/banka/admin">Admin Dashboard</RouterLink></li>
        </ul>
      </li>
    </ul>

    <h3>Bank B</h3>
    <ul>
      <li>
        <strong>Client Banking Interface:</strong>
        <ul>
          <li><RouterLink to="/bankb">Client Dashboard</RouterLink></li>
          <li><RouterLink to="/bankb/transfer">Transfer Form</RouterLink></li>
        </ul>
      </li>
      <li>
        <strong>Administrative Interface:</strong>
        <ul>
          <li><RouterLink to="/bankb/admin">Admin Dashboard</RouterLink></li>
        </ul>
      </li>
    </ul>

    <h3>Transaction Monitoring</h3>
    <ul>
      <li>
        <strong>Transaction Views:</strong>
        <ul>
          <li>
            Transaction Status Tracking:
            <ul>
              <li>Bank A - Individual transaction status pages (/banka/admin/transfers/:uetr)</li>
              <li>Bank B - Individual transaction status pages (/bankb/admin/transfers/:uetr)</li>
            </ul>
          </li>
          <li>Transactions Dashboard (available in admin interfaces above)</li>
          <li>Cross-bank Transaction Visibility (Both Bank A & B)</li>
          <li>Audit Trail & Compliance Reporting (Both Bank A & B)</li>
        </ul>
      </li>
    </ul>

    <h2>Additional Monitoring Solutions</h2>
    <ul>
      <li>
        <strong>Infrastructure Monitoring:</strong>
        <ul>
          <li>Kafka: <em>linkTBD</em></li>
          <li>Block Explorer: <em>linkTBD</em></li>
        </ul>
      </li>
    </ul>

    <h3>About Exactpro</h3>

    <p>
      <a href="https://exactpro.com/" target="_blank" rel="noopener noreferrer">Exactpro</a>
      has been building testing and development expertise in the DLT/ Blockchain space since 2018. Since then, we have
      implemented multiple regulatory-grade testing and development projects with leading technology providers and
      digital exchanges disrupting traditional finance. Visit our website at https://exactpro.com/ to learn more.
    </p>

    <p>
      Should you have any follow-up queries or feedback, please reach out to the Exactpro team at
      <strong>info@exactpro.com</strong>.
    </p>
  </article>
</template>
